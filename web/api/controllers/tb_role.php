<?
    namespace tb_role;

    $API->get( '/roles/',          'tb_role\get_roles'   );
    $API->get( '/roles/{role}',    'tb_role\get_role'    );
    $API->post( '/roles/',         'tb_role\create_role' );
    $API->put( '/roles/{role}',    'tb_role\update_role' );
    $API->delete( '/roles/{role}', 'tb_role\delete_role' );

    const FIELDS = [
        'role',
        'name'
    ];

    function get_roles( $request, $response, $args )
    {
        $params = $request->getQueryParams();
        $query  = <<<SQL
select role,
       name
  from tb_role
 where true
SQL;

        if( count( $params ) > 0 )
        {
            foreach( $params as $name => $value )
            {
                if( !in_array( $name, FIELDS ) )
                    return invalid_field_error( $response, $name );

                $query .= " and $name = ?$name?";
            }
        }

        $resource = query_execute( $query, $params );

        if( query_success( $resource ) )
        {
            $data = query_fetch_all( $resource );

            if( empty( $data ) )
                $data = null;

            if( $data )
                $data = json_encode( $data );

            $response->withHeader( 'Content-Type', 'application/json' );

            return $response
                ->getBody()
                ->write( $data );
        }
        else
            return database_error( $response );
    }

    function get_role( $request, $response, $args )
    {
        $role  = $request->getAttribute( 'role' );
        $query = <<<SQL
select role,
       name
  from tb_role
 where role = ?role?
SQL;

        $params   = [ 'role' => $role ];
        $resource = query_execute( $query, $params );

        if( query_success( $resource ) )
        {
            $data = query_fetch_one( $resource );

            if( empty( $data ) )
                $data = null;

            if( $data )
                $data = json_encode( $data );

            $response->withHeader( 'Content-Type', 'application/json' );

            return $response
                ->getBody()
                ->write( $data );
        }
        else
            return database_error( $response );
    }

    function create_role( $request, $response, $args )
    {
        $params       = $request->getParsedBody();
        $query_fields = '';
        $query_values = '';

        foreach( $params as $name => $value )
        {
            if( !in_array( $name, FIELDS ) )
                return invalid_field_error( $response, $name );

            $query_fields .= "$name, ";
            $query_values .= "?$name?, ";
        }

        $query_fields = preg_replace( '/, $/', '', $query_fields );
        $query_values = preg_replace( '/, $/', '', $query_values );
        $query        = "insert into tb_role ( $query_fields ) values ( $query_values ) returning role";

        $resource = query_execute( $query, $params );

        if( query_success( $resource ) )
        {
            $data = query_fetch_one( $resource );
            $data = json_encode( $data );

            $response->withHeader( 'Content-Type', 'application/json' );

            return $response
                ->getBody()
                ->write( $data );
        }
        else
            return database_error( $response );
    }

    function update_role( $request, $response, $args )
    {
        $role = $request->getAttribute( 'role' );

        $params         = $request->getParsedBody();
        $params['role'] = $role;
        $query          = 'update tb_role set ';

        foreach( $params as $name => $value )
        {
            if( !in_array( $name, FIELDS ) )
                return invalid_field_error( $response, $name );

            $query .= "$name = ?$name?, ";
        }

        $query    = preg_replace( '/, $/', ' where role = ?role?', $query );
        $resource = query_execute( $query, $params );

        if( query_success( $resource ) )
        {
            $data = json_encode( [ 'success' => true ] );

            $response->withHeader( 'Content-Type', 'application/json' );

            return $response
                ->getBody()
                ->write( $data );
        }
        else
            return database_error( $response );
    }

    function delete_role( $request, $response, $args )
    {
        $role  = $request->getAttribute( 'role' );
        $query = <<<SQL
delete from tb_role
      where role = ?role?
SQL;

        $params   = [ 'role' => $role ];
        $resource = query_execute( $query, $params );

        if( query_success( $resource ) )
        {
            $data = json_encode( [ 'success' => true ] );

            $response->withHeader( 'Content-Type', 'application/json' );

            return $response
                ->getBody()
                ->write( $data );
        }
        else
            return database_error( $response );
    }
?>
