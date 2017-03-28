<?
    namespace events;

    $API->get( '/events/',           'events\get_events'   );
    $API->get( '/events/{event}',    'events\get_event'    );
    $API->post( '/events/',          'events\create_event' );
    $API->put( '/events/{event}',    'events\update_event' );
    $API->delete( '/events/{event}', 'events\delete_event' );

    function get_events( $request, $response, $args )
    {
        $params = $request->getQueryParams();

        $query = <<<SQL
select event,
       name,
       start_time,
       end_time,
       created
  from tb_event
 where true
SQL;

        $valid_fields = [
            'event',
            'name',
            'start_time',
            'end_time',
            'created'
        ];

        foreach( $params as $name => $value )
        {
            if( !in_array( $name, $valid_fields ) )
                return invalid_field_error( $response, $name );

            $query .= " and $name = ?$name?";
        }

        return api_fetch_all( $response, $query, $params );
    }

    function get_event( $request, $response, $args )
    {
        $event  = $request->getAttribute( 'event' );
        $params = [ 'event' => $event ];

        $query = <<<SQL
select event,
       name,
       start_time,
       end_time,
       created
  from tb_event
 where event = ?event?
SQL;

        $retval = api_fetch_one( $response, $query, $params );

        if( $retval === null )
            return object_not_found_error( $response, 'tb_event', $event );

        return $retval;
    }

    function create_event( $request, $response, $args )
    {
        $params = $request->getParsedBody();

        if( count( $params ) == 0 )
            return empty_params_error( $response );

        $valid_fields = [
            'name',
            'start_time',
            'end_time'
        ];

        $columns = '';
        $values  = '';

        foreach( $params as $name => $value )
        {
            if( !in_array( $name, $valid_fields ) )
                continue;

            $columns .= "$name, ";
            $values  .= "?$name?, ";
        }

        $columns = preg_replace( '/, $/', '', $columns );
        $values  = preg_replace( '/, $/', '', $values  );

        $query  = <<<SQL
insert into tb_event ( $columns )
     values ( $values )
  returning event;
SQL;

        begin_transaction();

        $resource = query_execute( $query, $params );

        if( query_success( $resource ) )
            $retval = query_fetch_one( $resource );
        else
        {
            rollback_transaction();
            return database_error( $response );
        }

        $create_owner_query = <<<SQL
insert into tb_event_entity_role
(
    event,
    entity,
    role
)
values
(
    ?event?,
    ?entity?,
    ?role?
)
SQL;

        $create_owner_params = [
            'event'  => $retval['event'],
            'entity' => $params['owner'],
            'role'   => constant( 'ROLE_OWNER' )
        ];

        $resource = query_execute( $create_owner_query, $create_owner_params );

        if( query_success( $resource ) )
        {
            commit_transaction();
            return $response->withJson( $retval );
        }
        else
        {
            rollback_transaction();
            return database_error( $response );
        }
    }

    function update_event( $request, $response, $args )
    {
        $event  = $request->getAttribute( 'event' );
        $params = $request->getParsedBody();

        if( count( $params ) == 0 )
            return empty_params_error( $response );

        $valid_fields = [
            'name',
            'start_time',
            'end_time',
            'created'
        ];

        $assignments  = '';

        foreach( $params as $name => $value )
        {
            if( !in_array( $name, $valid_fields ) )
                return invalid_field_error( $response, $name );

            $assignments .= "$name = ?$name?, ";
        }

        $assignments = preg_replace( '/, $/', '', $assignments );

        $query = <<<SQL
   update tb_event
      set $assignments
    where event = ?event?
returning event;
SQL;

        $params['event'] = $event;
        $retval          = api_fetch_one( $response, $query, $params );

        if( $retval === null )
            return object_not_found_error( $response, 'tb_event', $event );

        return $retval;
    }

    function delete_event( $request, $response, $args )
    {
        $event  = $request->getAttribute( 'event' );
        $params = [ 'event' => $event ];
        $query  = <<<SQL
delete from tb_event
      where event = ?event?
  returning event
SQL;

        $retval = api_fetch_one( $response, $query, $params );

        if( $retval === null )
            return object_not_found_error( $response, 'tb_event', $event );

        return $retval;
    }
?>
