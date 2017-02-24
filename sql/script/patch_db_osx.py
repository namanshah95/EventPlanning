#!/usr/local/bin/python

# Before running, make sure of the following:
# 1. tb_applied_patch exists as specified in data/2016-11/issue-1.patch_metadata_schema.sql
# 2. fn_insert_or_update_row() exists as specified in functions/fn_insert_or_update_row.sql

import getopt
import hashlib
import psycopg2
import os
import re
import subprocess
import sys

IGNORE_DIRECTIVE   = r'^--\s*ignore$'
REQUIRES_DIRECTIVE = r'^--\s*requires:\s*(.+)$'

GET_PATCH_RECORD_CHECKSUM = """
select checksum
  from tb_applied_patch
 where patch_folder = %(patch_folder)s
   and patch_file   = %(patch_file)s
"""

INSERT_OR_UPDATE_PATCH_RECORD_QUERY = """
select fn_insert_or_update_row(
           'tb_applied_patch',
           '{
              %s
            }'::json,
           array[ 'patch_folder', 'patch_file' ]
       )
"""

SQL_PATH = os.path.dirname( os.path.realpath( __file__ ) )

db_conn  = None
database = None
user     = None

def file_checksum( patch_file ):
    patch_fh = open( patch_file, 'rb' )
    hash_gen = hashlib.md5()
    buf      = patch_fh.read( 65536 )

    while len( buf ) > 0:
        hash_gen.update( buf )
        buf = patch_fh.read( 65536 )

    patch_fh.close()
    return hash_gen.hexdigest()

def insert_or_update_patch_record( patch_folder, patch_file, is_function, checksum, reapplied ):
    global db_conn

    params_string = '"patch_folder" : "%s", "patch_file" : "%s", "is_function" : %s' % (
        patch_folder,
        patch_file,
        str( is_function ).lower()
    )

    if checksum:
        params_string += ', "checksum" : "%s"' % ( checksum )

    if reapplied:
        params_string += ', "reapplied" : "%s"' % ( reapplied )

    upsert_query = INSERT_OR_UPDATE_PATCH_RECORD_QUERY % params_string

    db_cursor = db_conn.cursor()
    db_cursor.execute( upsert_query )
    db_conn.commit()

def get_ordered_patches( patch_folder ):
    hierarchy = {}

    for patch_file in os.listdir( SQL_PATH + '/' + patch_folder ):
        patch_fh       = open( SQL_PATH + '/' + patch_folder + '/' + patch_file, 'r' )
        directives_remain = True

        while directives_remain:
            directive = patch_fh.readline()

            ignore_match   = re.match( IGNORE_DIRECTIVE, directive )
            requires_match = re.match( REQUIRES_DIRECTIVE, directive )

            if ignore_match:
                directives_remain = False
            elif requires_match:
                if patch_file not in hierarchy:
                    hierarchy[patch_file] = []

                required_function = requires_match.group( 1 )
                hierarchy[patch_file].append( required_function )
            else:
                if patch_file not in hierarchy:
                    hierarchy[patch_file] = None

                directives_remain = False

        patch_fh.close()

    return hierarchy

def get_patch_record_checksum( patch_file, patch_folder ):
    global db_conn

    params = {
        'patch_file'   : patch_file,
        'patch_folder' : patch_folder
    }

    db_cursor = db_conn.cursor()
    db_cursor.execute( GET_PATCH_RECORD_CHECKSUM, params )
    retval = db_cursor.fetchone()
    return retval[0] if retval else None

def apply_patch( patch_folder, patch_file ):
    global database, user

    full_patch_name = SQL_PATH + '/' + patch_folder + '/' + patch_file
    output          = subprocess.check_output(
        [ 'psql', '-w', '-U', user, '-d', database, '-f', full_patch_name, '-1' ],
        stderr=subprocess.STDOUT,
    )

    print output

    for line in iter( output.splitlines() ):
        if 'ERROR:' in line:
            return False

    return True

def apply_patch_with_requires( hierarchy, patch_file, patch_folder ):
    try:
        requires = hierarchy.pop( patch_file )
    except KeyError:
        requires = None

    full_patch_path = patch_folder + '/' + patch_file
    reapplied       = None

    if patch_folder != 'functions':
        existing_checksum = get_patch_record_checksum( patch_file, patch_folder )
        checksum          = file_checksum( SQL_PATH + '/' + full_patch_path )

        if existing_checksum and existing_checksum == checksum:
            return
        elif existing_checksum and existing_checksum != checksum:
            applying_updated = True
        else:
            applying_updated = False

        is_function = False
    else:
        is_function      = True
        checksum         = None
        applying_updated = False

    if requires and len( requires ) > 0:
        for required_patch in requires:
            apply_patch_with_requires( hierarchy, required_patch, patch_folder )

    if applying_updated:
        do_apply_updated = ''
        print( 'The patch ' + full_patch_path + ' has been modified since last applied. Re-apply?' ),

        while do_apply_updated.lower() not in [ 'y', 'n' ]:
            do_apply_updated = raw_input( '(y/n) ' )

        if do_apply_updated.lower() == 'y':
            print 'Applying updated patch: ' + full_patch_path
            reapplied = 'now()'
        else:
            return
    else:
        if is_function:
            print 'Installing function: ' + full_patch_path
        else:
            do_apply = ''
            print( 'Apply patch ' + full_patch_path + '?' ),

            while do_apply.lower() not in [ 'y', 'n' ]:
                do_apply = raw_input( '(y/n) ' )

            if do_apply.lower() == 'y':
                print 'Applying patch: ' + full_patch_path
            else:
                return

    patch_success = apply_patch( patch_folder, patch_file )

    if patch_success:
        insert_or_update_patch_record( patch_folder, patch_file, is_function, checksum, reapplied )
    else:
        print 'Patch failed to apply.\n'

def usage():
    print 'Usage: ./patch_db.py -d <database> -U <username>'
    exit( 0 )

def main( argv ):
    global db_conn, database, user

    try:
        opts, args = getopt.getopt( argv, 'hd:U:' )
    except getopt.GetoptError:
        usage()

    for opt, arg in opts:
        if opt == '-h':
            usage()
        elif opt == '-d':
            database = arg
        elif opt == '-U':
            user = arg

    if not database or not user:
        usage()

    db_conn = psycopg2.connect(
        database = database,
        user     = user
    )

    print 'Applying patches...\n'

    for patch_folder in os.listdir( '../schema' ):
        patch_hierarchy = get_ordered_patches( '../schema/' + patch_folder )

        while len( patch_hierarchy.keys() ) > 0:
            patch_file = sorted( patch_hierarchy.keys() )[0]
            apply_patch_with_requires( patch_hierarchy, patch_file, '../schema/' + patch_folder )

    print 'Installing functions...\n'

    function_hierarchy = get_ordered_patches( '../functions' )

    while len( function_hierarchy.keys() ) > 0:
        patch_file = sorted( function_hierarchy.keys() )[0]
        apply_patch_with_requires( function_hierarchy, patch_file, '../functions' )

if __name__ == "__main__":
    main( sys.argv[1:] )

sys.exit( 0 )
