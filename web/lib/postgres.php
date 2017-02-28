<?
    function get_or_connect_to_db()
    {
        if( !isset( $GLOBALS['db_handle']) )
            $GLOBALS['db_handle'] = pg_connect( PSQL_CONNECT_STRING );

        return $GLOBALS['db_handle'];
    }

    /*
     * Prepare a query for pg_prepare().
     * First, this finds all question mark-escaped params in $query and builds
     * an array of their equivalent values corresponding to index.
     * Next, this replaces all question-mark escaped params in $query with
     * $counters.
     * Finally, this returns the query and param array we built.
     *
     * Example query: SELECT *
     *                  FROM tb_blog_post
     *                 WHERE blog_post = ?num?
     *                    OR title     = ?title?
     *                    OR member    = ?num?
     * Example params: [ 'title' => 'Test Title', 'num' => 30 ]
     *
     * The first step builds an array like this:
     *   [ 30, 'Test Title', 30 ]
     * The second step turns the query into this:
     *   SELECT *
     *     FROM tb_blog_post
     *    WHERE blog_post = $1
     *       OR title     = $2
     *       OR member    = $3
     * And the modified query and constructed param array are returned.
     *
     * The resulting return query and return params are suitable for use with
     * pg_prepare() or pg_query_params().
     *
     * Params:
     *   $query  : string                    - the query to prepare
     *   $params : array[ string => scalar ] - the parameters the query uses
     * Returns:
     *   [
     *     0 => The prepared query
     *     1 => The prepared parameters array
     *   ]
     */
    function __query_prep_params( $query, $params )
    {
        $params_array = [];

        // Replace each ?param? with a $counter
        $query_prepped = preg_replace_callback(
            '/\?(.+?)\?/',
            function( $match ) use( &$params_array, &$params )
            {
                // First time this function is called, "$1" is returned
                // Second time, "$2" is returned, and so forth
                static $i = 0;

                // At the same time, add the corresponding value to the params array
                array_push( $params_array, $params[$match[1]] );
                return '$' . ++$i;
            },
            $query
        );

        return [ $query_prepped, $params_array ];
    }

    /*
     * Prepares and executes a SQL query.
     *
     * Parameters:
     *   $query  : string                    - the SELECT query to execute
     *   $params : array[ string => scalar ] - the parameters the query uses (default false)
     * Returns:
     *   <<PG resource object>> on success,
     *   <<false>> otherwise.
     */
    function query_execute( $query, $params=false )
    {
        if( $params )
        {
            list( $query_prepped, $params_array ) = __query_prep_params( $query, $params );
            $result = pg_query_params( $query_prepped, $params_array );
        }
        else
            $result = pg_query( $query );

        if( $result )
            return $result;
        else
        {
            error_log( pg_result_error( $result ) );
            return false;
        }
    }

    /*
     * Wrapper for pg_fetch_assoc().
     *
     * Parameters:
     *   $resource : PG resource - the result of a successful query_execute() call
     * Returns:
     *   <<an array mapping columns to values>> on success if there is a row left to fetch;
     *   <<false>> otherwise.
     */
    function query_fetch_one( $resource )
    {
        $retval = pg_fetch_assoc( $resource );

        if( $retval === false )
            return null;

        return $retval;
    }

    /*
     * Wrapper for pg_fetch_all().
     *
     * Parameters:
     *   $resource : PG resource - the result of a successful query_execute() call
     * Returns:
     *   <<an array of [arrays mapping columns to values]>> on success if there are rows to fetch;
     *   <<false>> otherwise.
     */
    function query_fetch_all( $resource )
    {
        $retval = pg_fetch_all( $resource );

        if( $retval === false )
            return [];

        return $retval;
    }

    /*
     * Wrapper for is_resource().
     *
     * Parameters:
     *   $resource : PG resource - the result of a call to query_execute()
     * Returns:
     *   <<true>> if the query was successful;
     *   <<false>> otherwise.
     */
    function query_success( $resource )
    {
        return is_resource( $resource );
    }

    function begin_transaction()
    {
        return pg_query( 'begin' );
    }

    function commit_transaction()
    {
        return pg_query( 'commit' );
    }

    function rollback_transaction()
    {
        return pg_query( 'rollback' );
    }
?>
