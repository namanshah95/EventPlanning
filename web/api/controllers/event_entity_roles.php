<?
    namespace event_entity_roles;

    /* EVENT -> GUEST */
    $API->get( '/event/{event}/guests/',           'event_entity_roles\get_event_guests'        );
    $API->get( '/event/{event}/guests/{guest}',    'event_entity_roles\get_event_guest'         );
    $API->post( '/event/{event}/guests/',          'event_entity_roles\add_guest_to_event'      );
    $API->delete( '/event/{event}/guests/{guest}', 'event_entity_roles\delete_guest_from_event' );

    /* EVENT -> OWNER */
    $API->get( '/event/{event}/owner/', 'event_entity_roles\get_event_owner' );

    /* EVENT -> ENTITY */
    $API->post( '/event/{event}/entities/{entity}/roles/',      'event_entity_roles\add_role_to_event_entity' );
    $API->put( '/event/{event}/entities/{entity}/roles/{role}', 'event_entity_roles\update_event_entity_role' );

    /* ENTITY -> EVENT */
    $API->get( '/entity/{entity}/events/', 'event_entity_roles\get_events_by_entity' );

    function get_event_guests( $request, $response, $args )
    {
        $event  = $request->getAttribute( 'event' );
        $params = $request->getQueryParams();

        // This query excludes the event owner from appearing in the result set
        $query = <<<SQL
with tt_owner as
(
    select e.entity
      from tb_entity e
      join tb_event_entity_role eer
        on e.entity = eer.entity
     where eer.event = ?event?
       and eer.role = ?ROLE_OWNER?
)
select eer.event_entity_role,
       eer.event,
       e.name as event_name,
       eer.entity,
       eer.role,
       r.name as role_name,
       eer.estimated_budget
  from tb_event_entity_role eer
  join tb_event e
    on eer.event = e.event
  join tb_role r
    on eer.role = r.role
  join tt_owner tt
    on eer.entity <> tt.entity
 where eer.event = ?event?
SQL;

        $valid_fields = [
            'event_entity_role' => 'eer.event_entity_role',
            'entity'            => 'eer.entity',
            'role'              => 'eer.role',
            'role_name'         => 'r.name',
            'estimated_budget'  => 'eer.estimated_budget'
        ];

        foreach( $params as $name => $value )
        {
            if( !in_array( $name, array_keys( $valid_fields ) ) )
                return invalid_field_error( $response, $name );

            $query .= " and {$valid_fields[$name]} = ?$name?";
        }

        $params['event']      = $event;
        $params['ROLE_OWNER'] = constant( 'ROLE_OWNER' );
        $params['ROLE_GUEST'] = constant( 'ROLE_GUEST' );

        return api_fetch_all( $response, $query, $params );
    }

    function get_event_guest( $request, $response, $args )
    {
        $event = $request->getAttribute( 'event' );
        $guest = $request->getAttribute( 'guest' );

        $params = [
            'event'      => $event,
            'guest'      => $guest,
            'ROLE_OWNER' => constant( 'ROLE_OWNER' ),
            'ROLE_GUEST' => constant( 'ROLE_GUEST' )
        ];

        // This query excludes the event owner from appearing in the result set
        $query = <<<SQL
with tt_owner as
(
    select e.entity
      from tb_entity e
      join tb_event_entity_role eer
        on e.entity = eer.entity
     where eer.event = ?event?
       and eer.role = ?ROLE_OWNER?
)
select eer.event_entity_role,
       eer.event,
       e.name as event_name,
       eer.entity,
       eer.role,
       r.name as role_name,
       eer.estimated_budget
  from tb_event_entity_role eer
  join tb_event e
    on eer.event = e.event
  join tb_role r
    on eer.role = r.role
  join tt_owner tt
    on eer.entity <> tt.entity
 where eer.event = ?event?
   and eer.entity = ?guest?
SQL;

        return api_fetch_all( $response, $query, $params );
    }

    function add_guest_to_event( $request, $response, $args )
    {
        $event = $request->getAttribute( 'event' );
        $params = $request->getParsedBody();

        $params['event']      = $event;
        $params['ROLE_GUEST'] = constant( 'ROLE_GUEST' );

        $query = <<<SQL
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
    ?ROLE_GUEST?
)
returning event_entity_role
SQL;

        return api_fetch_one( $response, $query, $params );
    }

    function delete_guest_from_event( $request, $response, $args )
    {
        $event  = $request->getAttribute( 'event' );
        $guest  = $request->getAttribute( 'guest' );

        $params = [
            'event'  => $event,
            'entity' => $guest
        ];

        $query = <<<SQL
delete from tb_event_entity_role
      where event  = ?event?
        and entity = ?entity?
  returning event_entity_role
SQL;

        return api_fetch_all( $response, $query, $params );
    }

    function add_role_to_event_entity( $request, $response, $args )
    {
        $event  = $request->getAttribute( 'event' );
        $guest  = $request->getAttribute( 'guest' );
        $params = $request->getParsedBody();

        if( count( $params ) == 0 )
            return empty_params_error( $response );

        $params['event']  = $event;
        $params['entity'] = $guest;

        $valid_fields = [
            'event',
            'entity',
            'role',
            'estimated_budget'
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
insert into tb_event_entity_role ( $columns )
     values ( $values )
  returning event_entity_role;
SQL;

        return api_fetch_one( $response, $query, $params );
    }

    function get_event_owner( $request, $response, $args )
    {
        $event  = $request->getAttribute( 'event' );
        $params = $request->getQueryParams();

        $query = <<<SQL
with tt_owner as
(
    select e.entity
      from tb_entity e
      join tb_event_entity_role eer
        on e.entity = eer.entity
     where eer.event = ?event?
       and eer.role = ?ROLE_OWNER?
)
select eer.event_entity_role,
       eer.event,
       e.name as event_name,
       eer.entity,
       eer.role,
       r.name as role_name,
       eer.estimated_budget
  from tb_event_entity_role eer
  join tb_event e
    on eer.event = e.event
  join tb_role r
    on eer.role = r.role
  join tt_owner tt
    on eer.entity = tt.entity
 where eer.event = ?event?
SQL;

        $valid_fields = [
            'event_entity_role' => 'eer.event_entity_role',
            'entity'            => 'eer.entity',
            'role'              => 'eer.role',
            'role_name'         => 'r.name',
            'estimated_budget'  => 'eer.estimated_budget'
        ];

        foreach( $params as $name => $value )
        {
            if( !in_array( $name, array_keys( $valid_fields ) ) )
                return invalid_field_error( $response, $name );

            $query .= " and {$valid_fields[$name]} = ?$name?";
        }

        $params['event']      = $event;
        $params['ROLE_OWNER'] = constant( 'ROLE_OWNER' );

        return api_fetch_all( $response, $query, $params );
    }

    function get_events_by_entity( $request, $response, $args )
    {
        $entity = $request->getAttribute( 'entity' );
        $params = $request->getQueryParams();

        $query  = <<<SQL
select eer.event_entity_role,
       eer.event,
       e.name as event_name,
       eer.entity,
       eer.role,
       r.name as role_name,
       eer.estimated_budget
  from tb_event_entity_role eer
  join tb_event e
    on eer.event = e.event
  join tb_role r
    on eer.role = r.role
 where eer.entity = ?entity?
SQL;

        $valid_fields = [
            'event_entity_role' => 'eer.event_entity_role',
            'entity'            => 'eer.entity',
            'role'              => 'eer.role',
            'role_name'         => 'r.name',
            'estimated_budget'  => 'eer.estimated_budget'
        ];

        foreach( $params as $name => $value )
        {
            if( !in_array( $name, array_keys( $valid_fields ) ) )
                return invalid_field_error( $response, $name );

            $query .= " and {$valid_fields[$name]} = ?$name?";
        }

        $params['entity'] = $entity;
        return api_fetch_all( $response, $query, $params );
    }

    function update_event_entity_role( $request, $response, $args )
    {
        $event  = $request->getAttribute( 'event' );
        $entity = $request->getAttribute( 'entity' );
        $role   = $request->getAttribute( 'role' );

        $params = $request->getParsedBody();

        if( count( $params ) == 0 )
            return empty_params_error( $response );

        $valid_fields = [
            'estimated_budget'
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
   update tb_event_entity_role
      set $assignments
    where event  = ?event?
      and entity = ?entity?
      and role   = ?role?
returning event_entity_role
SQL;

        $params['event']  = $event;
        $params['entity'] = $entity;
        $params['role']   = $role;

        $retval = api_fetch_one( $response, $query, $params );

        if( $retval === null )
            return object_not_found_error( $response, 'tb_event_entity_role', $role );

        return $retval;
    }
?>
