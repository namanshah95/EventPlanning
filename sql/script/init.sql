create sequence sq_pk_applied_patch;
create table tb_applied_patch
(
    applied_patch integer      primary key default nextval( 'sq_pk_applied_patch'::regclass ),
    patch_folder  varchar(30)  not null,
    patch_file    varchar(200) not null,
    is_function   boolean      default false,
    checksum      varchar(32),
    first_applied timestamp    not null default now(),
    reapplied     timestamp,
    unique( patch_folder, patch_file ),
    check( checksum is not null or is_function is true )
);

create or replace function fn_insert_or_update_row
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
    select regexp_replace( in_table, '^tb_', '' )
      into my_pk_col;

    -- Get the existing PK (if it exists) by using the identifying
    -- columns and their respective values we passed in
    my_select := 'SELECT ' || my_pk_col || ' FROM ' || in_table || ' WHERE TRUE';

    foreach my_col in array in_identifying_cols
    loop
        my_select := my_select || ' AND ' || my_col || ' = ' || coalesce( quote_literal( in_col_values->>my_col ), 'null' );
    end loop;

    execute my_select
       into my_pk_val;

    -- If a row with those values already exists, then update it using what we passed in
    if my_pk_val is not null then
        my_query := 'UPDATE ' || in_table || ' SET ';

        for my_col, my_val in
            select *
              from json_each_text( in_col_values )
        loop
            my_query := my_query || my_col || ' = ' || quote_literal( my_val ) || ', ';
        end loop;

        select trim( trailing ', ' from my_query )
          into my_query;

        my_query := my_query || ' WHERE ' || my_pk_col || ' = ' || quote_literal( my_pk_val );

        execute my_query;
    else -- Otherwise, insert a new row entirely
        my_query  := 'INSERT INTO ' || in_table || ' ( ';
        my_values := 'VALUES ( ';

        for my_col, my_val in
            select *
              from json_each_text( in_col_values )
        loop
            my_query  := my_query || my_col || ', ';
            my_values := my_values || coalesce( quote_literal( my_val ), 'null' ) || ', ';
        end loop;

        select trim( trailing ', ' from my_query )
          into my_query;

        select trim( trailing ', ' from my_values )
          into my_values;

        my_query := my_query || ' ) ' || my_values || ' ) returning ' || my_pk_col;

        execute my_query
           into my_pk_val;
    end if;

    -- Return the PK of what we just inserted/updated
    return my_pk_val;
end
 $_$
language plpgsql;
