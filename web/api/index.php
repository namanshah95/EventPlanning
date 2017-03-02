<?
    if( isset( $_SERVER['CONTEXT_DOCUMENT_ROOT'] ) && $_SERVER['CONTEXT_DOCUMENT_ROOT'] )
        $GLOBALS['webroot'] = $_SERVER['CONTEXT_DOCUMENT_ROOT'];
    elseif( isset( $_SERVER['DOCUMENT_ROOT'] ) && $_SERVER['DOCUMENT_ROOT'] )
        $GLOBALS['webroot'] = $_SERVER['DOCUMENT_ROOT'];

    require_once( "{$GLOBALS['webroot']}/vendor/autoload.php" );
    require_once( "{$GLOBALS['webroot']}/lib/constants.php" );
    require_once( "{$GLOBALS['webroot']}/lib/postgres.php" );
    require_once( "{$GLOBALS['webroot']}/lib/api_controller.php" );

    get_or_connect_to_db();

    $config = [
        'settings' => [
            'displayErrorDetails'    => true,
            'addContentLengthHeader' => false
        ]
    ];

    $API = new \Slim\App( $config );

    $controllers = scandir( "{$GLOBALS['webroot']}/api/controllers" );
    $controllers = array_diff( $controllers, [ '..', '.' ] );

    foreach( $controllers as $controller )
        require_once( "{$GLOBALS['webroot']}/api/controllers/$controller" );

    $API->run();
?>
