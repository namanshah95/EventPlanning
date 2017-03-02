<?
    define( 'PSQL_HOST',           'localhost'     );
    define( 'PSQL_PORT',           '5432'          );
    define( 'PSQL_DB'  ,           'planmyth_dev'  );
    define( 'PSQL_USER',           'planmyth_user' );
    // I understand this is really, really bad. But we need this working, not
    // functional. I can't be bothered to figure out why Bluehost's apache
    // can't detect my .pgpass file. Need to call customer support later and
    // ask them what to do.
    define( 'PSQL_PASS',           'EsVNg4EGuk'    );
    define( 'PSQL_CONNECT_STRING', ' host='     . constant( 'PSQL_HOST' )
                                 . ' port='     . constant( 'PSQL_PORT' )
                                 . ' dbname='   . constant( 'PSQL_DB' )
                                 . ' user='     . constant( 'PSQL_USER' )
                                 . ' password=' . constant( 'PSQL_PASS' ) );

    define( 'HTTP_BAD_REQUEST',           400 );
    define( 'HTTP_INTERNAL_SERVER_ERROR', 500 );
?>
