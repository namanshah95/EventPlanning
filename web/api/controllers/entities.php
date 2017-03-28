<?
    namespace entities;

    $API->get( '/entities/',         'entities\get_entities' );
    $API->get( '/entities/{entity}', 'entities\get_entity'   );
    $API->post( '/entities/',        'entities\add_entity'   );

    function get_entities( $request, $response, $args )
    {
        $params = $request->getQueryParams();

        $name  = array_key_exists( 'Name', $params )  ? $params['Name']  : false;
        $email = array_key_exists( 'Email', $params ) ? $params['Email'] : false;

        $firebase = new \Firebase\FirebaseLib( constant( 'FIREBASE_URL' ), constant( 'FIREBASE_AUTHKEY' ) );
        $record   = $firebase->get( "/users/" );
        $record   = json_decode( $record, true );

        $result_set   = [];
        $firebase_ids = [];

        foreach( array_keys( $record ) as $firebase_id )
            $firebase_ids[] = "'$firebase_id'";

        $firebase_ids_str = implode( ',', $firebase_ids );

        $query = <<<SQL
select ext_firebase_id,
       entity
  from tb_entity
 where ext_firebase_id in ($firebase_ids_str)
SQL;

        $resource = query_execute( $query );

        if( query_success( $resource ) )
        {
            $entities_pre = query_fetch_all( $resource );
            $entities     = [];

            foreach( $entities_pre as $entity )
                $entities[$entity['ext_firebase_id']] = $entity['entity'];
        }
        else
            return database_error( $response );

        foreach( $record as $firebase_id => $json )
        {
            if(
                   array_key_exists( $firebase_id, $entities )
                && (
                        ( !$name && !$email )
                     || ( $name  && $json['Name']  == $name )
                     || ( $email && $json['Email'] == $email )
                   )
              )
            {
                $firebase_ids[] = $firebase_id;

                $result                    = $json;
                $result['ext_firebase_id'] = $firebase_id;
                $result['entity']          = $entities[$firebase_id];
                $result_set[]              = $result;
            }
        }

        return $response->withJson( $result_set );
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

            $firebase = new \Firebase\FirebaseLib( constant( 'FIREBASE_URL' ), constant( 'FIREBASE_AUTHKEY' ) );
            $record   = $firebase->get( "/users/$ext_firebase_id" );

            $record           = json_decode( $record, true );
            $record['entity'] = $entity;

            return $response->withJson( $record );
        }
        else
            return database_error( $response );
    }

    function add_entity( $request, $response, $args )
    {
        $params = $request->getParsedBody();
        $query  = <<<SQL
insert into tb_entity
(
    ext_firebase_id
)
values
(
    ?ext_firebase_id?
)
returning entity
SQL;

        return api_fetch_one( $response, $query, $params );
    }
?>
