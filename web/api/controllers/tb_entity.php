<?
    namespace tb_entity;

    const FIELDS = [
        'entity',
        'first_name',
        'last_name',
        'phone_number',
        'email_address'
    ];

    function get_entities( $request, $response, $args )
    {
        $params = $request->getQueryParams();
        $query  = <<<SQL
select entity,
       first_name,
       last_name,
       phone_number,
       email_address
  from tb_entity
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

    function get_entity( $request, $response, $args )
    {
        $entity = $request->getAttribute( 'entity' );
        $query  = <<<SQL
select entity,
       first_name,
       last_name,
       phone_number,
       email_address
  from tb_entity
 where entity = ?entity?
SQL;

        $params   = [ 'entity' => $entity ];
        $resource = query_execute( $query, $params );

        if( query_success( $resource ) )
        {
            $data = query_fetch_one( $resource );

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

        return $response;
    }

    $API->get( '/entities/',         'tb_entity\get_entities' );
    $API->get( '/entities/{entity}', 'tb_entity\get_entity'   );
?>
