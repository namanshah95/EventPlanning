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
    define( 'HTTP_NOT_FOUND',             404 );
    define( 'HTTP_INTERNAL_SERVER_ERROR', 500 );

    define( 'ROLE_OWNER', -1 );
    define( 'ROLE_GUEST', -2 );

    define( 'FIREBASE_URL',     'https://event-planner-160406.firebaseio.com/' );
    define( 'FIREBASE_AUTHKEY', 'cKZjwjMSN66IYC4bC8IDYx4CqySfSKaLIXKvofsq'     );
?>
