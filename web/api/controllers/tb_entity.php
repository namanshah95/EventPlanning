<?
    namespace tb_entity;

    $API->get( '/entities/',         'tb_entity\get_entities' );
    $API->get( '/entities/{entity}', 'tb_entity\get_entity'   );

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
?>
