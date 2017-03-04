--requires: event_schema.sql

create sequence sq_pk_budget_item;
create sequence sq_pk_event_budget_item;

create table tb_budget_item
(
    budget_item    integer primary key default nextval( 'sq_pk_budget_item'::regclass ),
    name           varchar(50) not null,
    estimated_cost numeric(10, 2) not null
);

create table tb_event_budget_item
(
    event_budget_item integer primary key default nextval( 'sq_pk_event_budget_item'::regclass ),
    event             integer not null references tb_event       ( event ),
    budget_item       integer not null references tb_budget_item ( budget_item ),
    quantity_needed   integer default 1,
    unique( event, budget_item )
);

-- TODO - not sure how budget items and entities will interact. Need to figure this out later
