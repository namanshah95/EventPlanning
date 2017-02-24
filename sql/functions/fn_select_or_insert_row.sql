create or replace function fn_select_or_insert_row
(
    in_table            text,
    in_col_values       json,
    in_identifying_cols text[]
)
returns integer as
 $_$
declare
    my_select text;
    my_pk_col text;
    my_pk_val integer;
    my_col    text;
    my_val    text;
    my_query  text;
    my_values text;
begin
    -- Get the PK column name
    select trim( leading 'tb_' from in_table )
      into my_pk_col;

    -- Get the existing PK (if it exists) by using the identifying
    -- columns and their respective values we passed in
    my_select := 'SELECT ' || my_pk_col || ' FROM ' || in_table || ' WHERE TRUE';

    foreach my_col in array in_identifying_cols
    loop
        my_select := my_select || ' AND ' || my_col || ' = ' || quote_literal( in_col_values->>my_col );
    end loop;

    execute my_select
       into my_pk_val;

    -- If a row with those values does not exist, then create it
    if my_pk_val is null then
        my_query  := 'INSERT INTO ' || in_table || ' ( ';
        my_values := 'VALUES ( ';

        for my_col, my_val in
            select *
              from json_each_text( in_col_values )
        loop
            my_query  := my_query || my_col || ', ';
            my_values := my_values || quote_literal( my_val ) || ', ';
        end loop;

        select trim( trailing ', ' from my_query )
          into my_query;

        select trim( trailing ', ' from my_values )
          into my_values;

        my_query := my_query || ' ) ' || my_values || ' ) returning ' || my_pk_col;

        execute my_query
           into my_pk_val;
    end if;

    -- Return the PK of what we just selected/inserted
    return my_pk_val;
end
 $_$
language plpgsql;
