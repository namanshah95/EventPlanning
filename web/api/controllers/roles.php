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

        $fields = [
            'role',
            'name'
        ];

        $where_clauses = [];

        if( count( $params ) > 0 )
        {
            foreach( $params as $name => $value )
            {
                if( !in_array( $name, FIELDS ) )
                    return invalid_field_error( $response, $name );

                $where_clauses[] = "$name = ?$name?";
            }
        }

        $query = construct_simple_select( 'tb_role', $fields, $where_clauses );
        return api_fetch_all( $response, $query, $params );
    }

    function get_role( $request, $response, $args )
    {
        $role = $request->getAttribute( 'role' );

        $fields = [
            'role',
            'name'
        ];

        $query  = construct_simple_select( 'tb_role', $fields, 'role = ?role?' );
        $params = [ 'role' => $role ];

        return api_fetch_one_with_404( $response, $query, $params, 'tb_role', $role );
    }

    function create_role( $request, $response, $args )
    {
        $params = $request->getParsedBody();
        $query  = construct_simple_insert( 'tb_role', 'role', $params );

        return api_fetch_one( $response, $query, $params );
    }

    function update_role( $request, $response, $args )
    {
        $role   = $request->getAttribute( 'role' );
        $params = $request->getParsedBody();

        foreach( $params as $name => $value )
        {
            if( !in_array( $name, FIELDS ) )
                return invalid_field_error( $response, $name );
        }

        $query          = construct_simple_update( 'tb_role', 'role', $params, 'role = ?role?' );
        $params['role'] = $role;

        return api_fetch_one_with_404( $response, $query, $params, 'tb_role', $role );
    }

    function delete_role( $request, $response, $args )
    {
        $role = $request->getAttribute( 'role' );

        $query  = construct_simple_delete( 'tb_role', 'role', 'role = ?role?' );
        $params = [ 'role' => $role ];

        return api_fetch_one_with_404( $response, $query, $params, 'tb_role', $role );
    }
?>
