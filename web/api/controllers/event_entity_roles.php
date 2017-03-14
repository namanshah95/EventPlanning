<?
    namespace event_entity_roles;

    $API->get( '/event/{event}/guests/',                'event_entity_roles\get_event_entities'       );
    $API->get( '/event/{event}/guests/{guest}',         'event_entity_roles\get_event_entity'         );
    $API->post( '/event/{event}/guests/{guest}/roles/', 'event_entity_roles\add_role_to_event_entity' );

    function get_event_entities( $request, $response, $args )
    {
        $event  = $request->getAttribute( 'event' );
        $params = $request->getQueryParams();
        $query  = <<<SQL
select eer.event_entity_role,
       eer.event,
       e.name as event_name,
       eer.entity,
       eer.role,
       r.name as role_name
  from tb_event_entity_role eer
  join tb_event e
    on eer.event = e.event
  join tb_role r
    on eer.role = r.role
 where eer.event = ?event?
SQL;

        $validFields = [
            'event_entity_role',
            'entity',
            'role',
            'role_name'
        ];

        foreach( $params as $name => $value )
        {
            if( !in_array( $name, $valid_fields ) )
                return invalid_field_error( $response, $name );

            $query .= " and $name = ?$name?";
        }

        $params['event'] = $event;
        return api_fetch_all( $response, $query, $params );
    }

    function get_event_entity( $request, $response, $args )
    {
        $event = $request->getAttribute( 'event' );
        $guest = $request->getAttribute( 'guest' );

        $params = [
            'event' => $event,
            'guest' => $guest
        ];

        $query = <<<SQL
select eer.event_entity_role,
       eer.event,
       e.name as event_name,
       eer.entity,
       eer.role,
       r.name as role_name
  from tb_event_entity_role eer
  join tb_event e
    on eer.event = e.event
  join tb_role r
    on eer.role = r.role
 where eer.event = ?event?
   and eer.entity = ?guest?
SQL;

        $retval = api_fetch_all( $response, $query, $params );
        return $retval;
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
        $columns      = '';
        $values       = '';

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
?>
