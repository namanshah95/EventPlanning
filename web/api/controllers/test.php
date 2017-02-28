<?
    $API->get( '/hello/{name}', function( $request, $response, $args ) {
        $data      = $request->getQueryParams();
        $name      = $request->getAttribute( 'name' );
        $greetings = filter_var( $data['greeting'], FILTER_SANITIZE_STRING );

        $response->getBody()->write( "$greetings, $name!" );
        return $response;
    });
?>
