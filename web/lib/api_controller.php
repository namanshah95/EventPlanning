<?
    /* ERROR FUNCTIONS */

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

    function object_not_found_error( $response, $table, $pk, $identifier='primary key' )
    {
        $error = [ 'error' => "'$table' record with $identifier '$pk' not found." ];
        return $response->withJson( $error, constant( 'HTTP_NOT_FOUND' ) );
    }

    function empty_params_error( $response )
    {
        $error = [ 'error' => 'No parameters were given.' ];
        return $response->withJson( $error, constant( 'HTTP_BAD_REQUEST' ) );
    }

    /* API DATABASE FUNCTIONS */

    function api_fetch_all( $response, $query, $params )
    {
        $resource = query_execute( $query, $params );

        if( query_success( $resource ) )
        {
            $data = query_fetch_all( $resource );
            return $response->withJson( $data );
        }
        else
            return database_error( $response );
    }

    function api_fetch_one( $response, $query, $params )
    {
        $resource = query_execute( $query, $params );

        if( query_success( $resource ) )
        {
            $data = query_fetch_one( $resource );

            if( empty( $data ) )
                return null;

            return $response->withJson( $data );
        }
        else
            return database_error( $response );
    }
?>
