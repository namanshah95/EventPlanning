#!/bin/sh

# This assumes the user "planmyth_user" has already been created as superuser.

script_file=""
script_dir="$(dirname '$(readlink -f $0)')"

# SQL credentials
user="postgres"
db_user="planmyth_user"
db="planmyth_dev"
db_temp="planmyth_dev_temp"

# $1 = command to run
# $2 = database name (optional)
pg_cmd () {
    if [ -z $2 ]; then
        psql -w -U "$user" -c "$1"
    else
        psql -w -U "$user" -d "$2" -c "$1"
    fi
}

# $1 = file to run
# $2 = database name
pg_file () {
    psql -w -U "$user" -d "$2" -f "$1" -1
}

echo "Creating new database..."
dropdb -e --if-exists -U "$user" -w "$db_temp"
createdb -e -U "$user" -w "$db_temp"

echo "\nRunning schema init script..."
pg_file "$script_dir/init.sql" "$db_temp"
echo "\nRunning DB patcher..."
./patch_db.py -U "$user" -d "$db_temp" -h localhost

echo "\nDropping old database..."
dropdb -e --if-exists -U "$user" -w "$db"

echo "\nRenaming new database..."
pg_cmd "alter database $db_temp rename to $db"

echo "\nGranting permissions..."
pg_cmd "grant usage on schema public to $db_user"
pg_cmd "alter default privileges in schema public grant all on tables to $db_user"
pg_cmd "grant connect on database $db to $db_user"

pg_cmd "grant usage on schema public to $db_user" "$db"
pg_cmd "grant all on all sequences in schema public to $db_user" "$db"
pg_cmd "grant all on all tables in schema public to $db_user" "$db"

echo "\nCreating extensions..."
pg_cmd "create extension pgcrypto" "$db"

exit 0
