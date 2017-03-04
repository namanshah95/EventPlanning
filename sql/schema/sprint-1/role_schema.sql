create sequence sq_pk_role;

create table tb_role
(
    role        integer primary key default nextval( 'sq_pk_role'::regclass ),
    name        varchar(50) not null unique,
    description text
);

insert into tb_role ( role, name,    description )
             values ( -1,   'Owner', 'The default role for the owner of an event.' );
