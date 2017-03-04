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

    function object_not_found_error( $response, $table, $pk )
    {
        $error = [ 'error' => "'$table' record with primary key '$pk' not found." ];
        return $response->withJson( $error, constant( 'HTTP_NOT_FOUND' ) );
    }

    /* SIMPLE SQL BUILDING FUNCTIONS */

    /*
     * These functions perform no error-checking. These functions do not check
     * to make sure no invalid field names are passed in, nor do they perform
     * any type checking.
     */

    function construct_simple_select( $table, $fields, $where=[] )
    {
        if( is_array( $fields ) )
            $fields_string = implode( ',',     $fields );
        else
            $fields_string = $fields;

        if( is_array( $where ) )
        {
            if( empty( $where ) )
                $where_string = 'true';
            else
                $where_string = implode( ' and ', $where );
        }
        else
            $where_string = $where;

        $query = <<<SQL
select $fields_string
  from $table
 where $where_string
SQL;

        return $query;
    }

    function construct_simple_insert( $table, $pk_col, $fields )
    {
        $query_fields = '';
        $query_values = '';

        foreach( $fields as $name => $value )
        {
            if( !in_array( $name, FIELDS ) )
                return invalid_field_error( $response, $name );

            $query_fields .= "$name, ";
            $query_values .= "?$name?, ";
        }

        $query_fields = preg_replace( '/, $/', '', $query_fields );
        $query_values = preg_replace( '/, $/', '', $query_values );

        $query = <<<SQL
insert into $table
(
    $query_fields
)
values
(
    $query_values
)
returning $pk_col
SQL;

        return $query;
    }

    function construct_simple_update( $table, $pk_col, $fields, $where )
    {
        $set = [];

        foreach( $params as $name => $value )
            $set[] = "$name = ?$name?";

        $set_string = implode( ',', $set );

        if( is_array( $where ) )
            $where_string = implode( ' and ', $where );
        else
            $where_string = $where;

        $query = <<<SQL
   update $table
      set $set_string
    where $where_string
returning $pk_col
SQL;

        return $query;
    }

    function construct_simple_delete( $table, $pk_col, $where )
    {
        if( is_array( $where ) )
            $where_string = implode( ' and ', $where );
        else
            $where_string = $where;

        $query = <<<SQL
delete from $table
      where $where_string
  returning $pk_col
SQL;

        return $query;
    }

    /* API DATABASE FUNCTIONS */

    function api_fetch_all( $response, $query, $params )
    {
        $resource = query_execute( $query, $params );

        if( query_success( $resource ) )
        {
            $data = query_fetch_all( $resource );

            if( empty( $data ) )
                $data = null;

            return $response->withJson( $data );
        }
        else
            return database_error( $response );
    }

    function api_fetch_one_with_404( $response, $query, $params, $table, $pk )
    {
        $resource = query_execute( $query, $params );

        if( query_success( $resource ) )
        {
            $data = query_fetch_one( $resource );

            if( empty( $data ) )
                return object_not_found_error( $response, $table, $pk );

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
            return $response->withJson( $data );
        }
        else
            return database_error( $response );
    }
?>
