<?
    namespace roles;

    $API->get( '/roles/',          'roles\get_roles'   );
    $API->get( '/roles/{role}',    'roles\get_role'    );
    $API->post( '/roles/',         'roles\create_role' );
    $API->put( '/roles/{role}',    'roles\update_role' );
    $API->delete( '/roles/{role}', 'roles\delete_role' );

    function get_roles( $request, $response, $args )
    {
        $params = $request->getQueryParams();
        $query  = <<<SQL
select role,
       name,
       description
  from tb_role
 where true
SQL;

        $valid_fields = [ 'role', 'name', 'description' ];

        foreach( $params as $name => $value )
        {
            if( !in_array( $name, $valid_fields ) )
                return invalid_field_error( $response, $name );

            $query .= " and $name = ?$name?";
        }

        return api_fetch_all( $response, $query, $params );
    }

    function get_role( $request, $response, $args )
    {
        $role   = $request->getAttribute( 'role' );
        $params = [ 'role' => $role ];
        $query  = <<<SQL
select role,
       name,
       description
  from tb_role
 where role = ?role?
SQL;

        $retval = api_fetch_one( $response, $query, $params );

        if( $retval === null )
            return object_not_found_error( $response, 'tb_role', $role );

        return $retval;
    }

    function create_role( $request, $response, $args )
    {
        $params = $request->getParsedBody();

        if( count( $params ) == 0 )
            return empty_params_error( $response );

        $valid_fields = [ 'role', 'name', 'description' ];
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

        $query  = <<<SQL
insert into tb_role ( $columns )
     values ( $values )
  returning role;
SQL;

        return api_fetch_one( $response, $query, $params );
    }

    function update_role( $request, $response, $args )
    {
        $role   = $request->getAttribute( 'role' );
        $params = $request->getParsedBody();

        if( count( $params ) == 0 )
            return empty_params_error( $response );

        $valid_fields = [ 'role', 'name', 'description' ];
        $assignments  = '';

        foreach( $params as $name => $value )
        {
            if( !in_array( $name, $valid_fields ) )
                return invalid_field_error( $response, $name );

            $assignments .= "$name = ?$name?, ";
        }

        $assignments = preg_replace( '/, $/', '', $assignments );

        $query = <<<SQL
   update tb_role
      set $assignments
    where role = ?role?
returning role;
SQL;

        $params['role'] = $role;
        $retval         = api_fetch_one( $response, $query, $params );

        if( $retval === null )
            return object_not_found_error( $response, 'tb_role', $role );

        return $retval;
    }

    function delete_role( $request, $response, $args )
    {
        $role   = $request->getAttribute( 'role' );
        $params = [ 'role' => $role ];
        $query  = <<<SQL
delete from tb_role
      where role = ?role?
  returning role
SQL;

        $retval = api_fetch_one( $response, $query, $params );

        if( $retval === null )
            return object_not_found_error( $response, 'tb_role', $role );

        return $retval;
    }
?>
