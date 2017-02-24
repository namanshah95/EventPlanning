--requires: entity_schema.sql
--requires: role_schema.sql

create sequence sq_tb_event_event;
create sequence sq_tb_event_role_event_role;
create sequence sq_tb_entity_event_entity_event;
create sequence sq_tb_entity_event_event_role_entity_event_event_role;

create table tb_event
(
    event      integer primary key default nextval( 'sq_tb_event_event'::regclass ),
    name       varchar(200) not null unique,
    start_time timestamp    not null,
    end_time   timestamp    not null,
    created    timestamp    not null default now()
);

create table tb_event_role
(
    event_role       integer primary key default nextval( 'sq_tb_event_role_event_role'::regclass ),
    event            integer not null references tb_event ( event ),
    role             integer not null references tb_role  ( role ),
    estimated_budget numeric(10, 2),
    quantity_needed  integer default 1,
    unique ( event, role )
);

create table tb_entity_event
(
    entity_event integer primary key default nextval( 'sq_tb_entity_event_entity_event'::regclass ),
    entity       integer not null references tb_entity  ( entity ),
    event        integer not null references tb_event ( event ),
    unique ( entity, event )
);

create table tb_entity_event_event_role
(
    entity_event_role integer primary key default nextval( 'sq_tb_entity_event_event_role_entity_event_event_role'::regclass ),
    entity_event      integer not null references tb_entity_event ( entity_event ),
    event_role        integer not null references tb_event_role ( event_role ),
    unique ( entity_event, event_role )
);
