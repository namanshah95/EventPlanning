<?
    function database_error( $response )
    {
        $error = [ 'error' => 'A database error has occurred.' ];
        return $response->withJson( $error, constant( 'HTTP_INTERNAL_SERVER_ERROR' ) );
    }

    function invalid_field_error( $response, $name )
    {
        $error = [ 'error' => "'$name' is not a valid field." ];
        return $response->withJson( $error, constant( 'HTTP_BAD_REQUEST' ) );
    }
    
    function object_not_found_error( $response, $table, $pk )
    {
        $error = [ 'error' => "'$table' record with primary key '$pk' not found." ];
        return $response->withJson( $error, constant( 'HTTP_NOT_FOUND' ) );
    }
?>
