<?
    namespace entities;

    $API->get( '/entities/{entity}', 'entities\get_entity' );

    function __firebase_fetch( $ext_firebase_id )
    {
        $firebase = new \Firebase\FirebaseLib( constant( 'FIREBASE_URL' ), constant( 'FIREBASE_AUTHKEY' ) );
        $record   = $firebase->get( "/users/$ext_firebase_id" );
        return $record;
    }

    function get_entity( $request, $response, $args )
    {
        $entity = $request->getAttribute( 'entity' );
        $params = [ 'entity' => $entity ];

        $query = <<<SQL
select ext_firebase_id
  from tb_entity
 where entity = ?entity?
SQL;

        $resource = query_execute( $query, $params );

        if( query_success( $resource ) )
        {
            $record = query_fetch_one( $resource );

            if( $record === null )
                return object_not_found_error( $response, 'tb_entity', $entity );

            $ext_firebase_id = $record['ext_firebase_id'];
            $record          = \entities\__firebase_fetch( $ext_firebase_id );

            $record           = json_decode( $record, true );
            $record['entity'] = $entity;

            return $response->withJson( $record );
        }
        else
            return database_error( $response );
    }
?>
