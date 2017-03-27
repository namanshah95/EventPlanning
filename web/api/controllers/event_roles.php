<?
    namespace event_roles;

    $API->get( '/event/{event}/roles/',          'event_roles\get_event_needed_roles'   );
    $API->get( '/event/{event}/roles/{role}',    'event_roles\get_event_needed_role'    ); // NEW
    $API->post( '/event/{event}/roles/',         'event_roles\create_event_needed_role' );
    $API->put( '/event/{event}/roles/{role}',    'event_roles\update_event_needed_role' );
    $API->delete( '/event/{event}/roles/{role}', 'event_roles\delete_event_needed_role' ); // NEW

    function get_event_needed_roles( $request, $response, $args )
    {
        $event  = $request->getAttribute( 'event' );
        $params = $request->getQueryParams();
        $query  = <<<SQL
select enr.event_needed_role,
       enr.event,
       enr.needed_role,
       r.name as needed_role_name,
       enr.estimated_budget,
       enr.quantity_needed,
       coalesce( enr.description, r.description ) as description
  from tb_event_needed_role enr
  join tb_role r
    on enr.needed_role = r.role
 where enr.event = ?event?
SQL;

        $valid_fields = [
            'event_needed_role' => 'enr.event_needed_role',
            'needed_role'       => 'enr.needed_role',
            'needed_role_name'  => 'r.name',
            'estimated_budget'  => 'enr.estimated_budget',
            'quantity_needed'   => 'enr.quantity_needed',
            'description'       => 'coalesce( enr.description, r.description )'
        ];

        foreach( $params as $name => $value )
        {
            if( !in_array( $name, array_keys( $valid_fields ) ) )
                return invalid_field_error( $response, $name );

            $query .= " and {$valid_fields[$name]} = ?$name?";
        }

        $params['event'] = $event;
        return api_fetch_all( $response, $query, $params );
    }

    function get_event_needed_role( $request, $response, $args )
    {
        $event  = $request->getAttribute( 'event' );
        $role   = $request->getAttribute( 'role' );

        $query  = <<<SQL
select enr.event_needed_role,
       enr.event,
       enr.needed_role,
       r.name as needed_role_name,
       enr.estimated_budget,
       enr.quantity_needed,
       coalesce( enr.description, r.description ) as description
  from tb_event_needed_role enr
  join tb_role r
    on enr.needed_role = r.role
 where enr.event = ?event?
   and enr.needed_role = ?role?
SQL;

        $params = [
            'event' => $event,
            'role'  => $role
        ];

        $retval = api_fetch_one( $response, $query, $params );

        if( $retval === null )
            return object_not_found_error( $response, 'tb_event_needed_role', $role, 'needed_role' );

        return $retval;
    }

    function create_event_needed_role( $request, $response, $args )
    {
        $event  = $request->getAttribute( 'event' );
        $params = $request->getParsedBody();

        if( count( $params ) == 0 )
            return empty_params_error( $response );

        $params['event'] = $event;

        $valid_fields = [
            'event',
            'needed_role',
            'estimated_budget',
            'quantity_needed',
            'description'
        ];

        $columns = '';
        $values  = '';

        foreach( $params as $name => $value )
        {
            if( !in_array( $name, $valid_fields ) )
                return invalid_field_error( $response, $name );

            $columns .= "$name, ";
            $values  .= "?$name?, ";
        }

        $columns = preg_replace( '/, $/', '', $columns );
        $values  = preg_replace( '/, $/', '', $values  );

        $query = <<<SQL
insert into tb_event_needed_role ( $columns )
     values ( $values )
  returning event_needed_role
SQL;

        return api_fetch_one( $response, $query, $params );
    }

    function update_event_needed_role( $request, $response, $args )
    {
        $event  = $request->getAttribute( 'event' );
        $role   = $request->getAttribute( 'role'  );
        $params = $request->getParsedBody();

        if( count( $params ) == 0 )
            return empty_params_error( $response );

        $valid_fields = [
            'estimated_budget',
            'quantity_needed',
            'description'
        ];

        $assignments = '';

        foreach( $params as $name => $value )
        {
            if( !in_array( $name, $valid_fields ) )
                return invalid_field_error( $response, $name );

            $assignments .= "$name = ?$name?, ";
        }

        $assignments = preg_replace( '/, $/', '', $assignments );

        $query = <<<SQL
   update tb_event_needed_role
      set $assignments
    where event = ?event?
      and needed_role = ?role?
returning event_needed_role;
SQL;

        $params['event'] = $event;
        $params['role']  = $role;
        $retval          = api_fetch_one( $response, $query, $params );

        if( $retval === null )
            return object_not_found_error( $response, 'tb_event_needed_role', $role );

        return $retval;
    }

    function delete_event_needed_role( $request, $response, $args )
    {
        $event  = $request->getAttribute( 'event' );
        $role   = $request->getAttribute( 'role'  );

        $query = <<<SQL
delete from tb_event_needed_role
      where event       = ?event?
        and needed_role = ?role?
  returning event_needed_role
SQL;

        $params = [
            'event' => $event,
            'role'  => $role
        ];

        $retval = api_fetch_one( $response, $query, $params );

        if( $retval === null )
            return object_not_found_error( $response, 'tb_event_needed_role', $role );

        return $retval;
    }
?>
