<?
    function database_error( $response )
    {
        return $response
            ->withStatus( constant( 'HTTP_INTERNAL_SERVER_ERROR' ) )
            ->withHeader( 'Content-Type', 'text/html' )
            ->write( 'A database error has occurred.' );
    }

    function invalid_field_error( $response, $name )
    {
        return $response
            ->withStatus( constant( 'HTTP_BAD_REQUEST' ) )
            ->withHeader( 'Content-Type', 'text/html' )
            ->write( "'$name' is not a valid field." );
    }
?>
