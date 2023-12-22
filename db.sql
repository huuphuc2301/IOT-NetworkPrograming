create table node
(
    id   varchar(55)  not null
        primary key,
    name varchar(255) null,
    status bool not null
);

create table temperature
(
    id         int auto_increment
        primary key,
    node_id    varchar(55) null,
    value      int         null,
    create_time timestamp   null,
    constraint node_temperature_node_id_fk
        foreign key (node_id) references node (id)
);

