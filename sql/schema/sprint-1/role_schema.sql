create sequence sq_tb_role_role;

create table tb_role
(
    role integer primary key default nextval( 'sq_tb_role_role'::regclass ),
    name varchar(50) not null unique
);

insert into tb_role ( role, name )
             values ( -1, 'Owner' );
