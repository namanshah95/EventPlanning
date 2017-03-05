--requires: entity_schema.sql
--requires: role_schema.sql

create sequence sq_pk_event;
create sequence sq_pk_event_needed_role;
create sequence sq_pk_event_entity_role;

create table tb_event
(
    event      integer primary key default nextval( 'sq_pk_event'::regclass ),
    name       varchar(200) not null unique,
    start_time timestamp    not null,
    end_time   timestamp    not null,
    created    timestamp    not null default now()
);

create table tb_event_needed_role
(
    event_needed_role integer primary key default nextval( 'sq_pk_event_needed_role'::regclass ),
    event             integer not null references tb_event ( event ),
    needed_role       integer not null references tb_role  ( role ),
    estimated_budget  numeric(10, 2),
    quantity_needed   integer default 1,
    description       text,
    unique ( event, needed_role )
);

create table tb_event_entity_role
(
    event_entity_role integer primary key default nextval( 'sq_pk_event_entity_role'::regclass ),
    event             integer not null references tb_event  ( event ),
    entity            integer not null references tb_entity ( entity ),
    role              integer not null references tb_role   ( role ),
    estimated_budget  numeric(10, 2),
    unique ( event, entity, role )
);
