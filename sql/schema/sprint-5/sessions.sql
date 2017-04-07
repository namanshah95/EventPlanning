create sequence sq_pk_entity_session;

create table tb_entity_session
(
    entity_session integer   primary key default nextval( 'sq_pk_entity_session'::regclass ),
    entity         integer   not null references tb_entity ( entity ),
    accessed       timestamp not null default now(),
    key            varchar   not null,
    value          text      not null
);

insert into tb_entity
(
    entity,
    ext_firebase_id
)
values
(
    -1,
    '-1'
);
