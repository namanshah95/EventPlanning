<?
    define( 'PSQL_HOST',           'localhost'     );
    define( 'PSQL_PORT',           '5432'          );
    define( 'PSQL_DB'  ,           'planmyth_dev'  );
    define( 'PSQL_USER',           'planmyth_user' );
    define( 'PSQL_CONNECT_STRING', ' host='     . constant( 'PSQL_HOST' )
                                 . ' port='     . constant( 'PSQL_PORT' )
                                 . ' dbname='   . constant( 'PSQL_DB' )
                                 . ' user='     . constant( 'PSQL_USER' ) );

    define( 'HTTP_BAD_REQUEST',           400 );
    define( 'HTTP_INTERNAL_SERVER_ERROR', 500 );
?>
