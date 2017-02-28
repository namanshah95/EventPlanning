<?
    namespace tb_role;

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
                {
                    return $response
                        ->withStatus( constant( 'HTTP_BAD_REQUEST' ) )
                        ->withHeader( 'Content-Type', 'text/html' )
                        ->write( "'$name' is not a valid field." );
                }

                $query .= " and $name = ?$name?";
            }
        }

        $resource = query_execute( $query, $params );

        if( query_success( $resource ) )
        {
            $data = query_fetch_all( $resource );

            if( $data )
                $data = json_encode( $data );

            $response->withHeader( 'Content-Type', 'application/json' );

            return $response
                ->getBody()
                ->write( $data );
        }
        else
        {
            return $response
                ->withStatus( constant( 'HTTP_INTERNAL_SERVER_ERROR' ) )
                ->withHeader( 'Content-Type', 'text/html' )
                ->write( 'A database error has occurred.' );
        }
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
            $data = json_encode( $data );

            $response->withHeader( 'Content-Type', 'application/json' );

            return $response
                ->getBody()
                ->write( $data );
        }
        else
        {
            return $response
                ->withStatus( constant( 'HTTP_INTERNAL_SERVER_ERROR' ) )
                ->withHeader( 'Content-Type', 'text/html' )
                ->write( 'A database error has occurred.' );
        }

        return $response;
    }

    $API->get( '/roles/',       'tb_role\get_roles' );
    $API->get( '/roles/{role}', 'tb_role\get_role'  );
?>
