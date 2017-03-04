create sequence sq_pk_entity;

create table tb_entity
(
    entity          integer primary key default nextval( 'sq_pk_entity'::regclass ),
    ext_firebase_id varchar(48) not null unique
);

