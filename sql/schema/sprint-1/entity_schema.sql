create sequence sq_tb_entity_entity;

create table tb_entity
(
    entity         integer primary key default nextval( 'sq_tb_entity_entity'::regclass ),
    first_name     varchar(50)  not null,
    last_name      varchar(50)  not null,
    phone_number   varchar(20)  not null unique,
    email_address  varchar(100) not null unique
);

-- TODO - add facebook_login field
