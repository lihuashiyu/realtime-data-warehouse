drop database if exists `maxwell`;
create database if not exists `maxwell`;


drop table if exists `maxwell`.`bootstrap`;
create table bootstrap
(
    id              bigint auto_increment primary key,
    database_name   varchar(255) charset utf8                     not null,
    table_name      varchar(255) charset utf8                     not null,
    where_clause    text,
    is_complete     tinyint unsigned            default '0'       not null,
    inserted_rows   bigint unsigned             default '0'       not null,
    total_rows      bigint unsigned             default '0'       not null,
    created_at      datetime,
    started_at      datetime,
    completed_at    datetime,
    binlog_file     varchar(255)                                  null,
    binlog_position int unsigned                default '0'       null,
    client_id       varchar(255) charset latin1 default 'maxwell' not null,
    comment         varchar(255) charset utf8                     null
);



drop table if exists `maxwell`.`columns`;
create table columns
(
    id            bigint auto_increment primary key,
    schema_id     bigint,
    table_id      bigint,
    name          varchar(255),
    charset       varchar(255),
    coltype       varchar(255),
    is_signed     tinyint unsigned,
    enum_values   text,
    column_length tinyint unsigned
);

create index schema_id on columns (schema_id);
create index table_id  on columns (table_id);


drop table if exists `maxwell`.`databases`;
create table `databases`
(
    id        bigint auto_increment primary key,
    schema_id bigint,
    name      varchar(255),
    charset   varchar(255)
);

create index schema_id on `databases` (schema_id);


drop table if exists `maxwell`.`heartbeats`;
create table heartbeats
(
    server_id int unsigned                   not null,
    client_id varchar(255) default 'maxwell' not null,
    heartbeat bigint                         not null,
    primary key (server_id, client_id)
);


drop table if exists `maxwell`.`positions`;
create table positions
(
    server_id           int unsigned                                  not null,
    binlog_file         varchar(255),
    binlog_position     int unsigned,
    gtid_set            varchar(4096),
    client_id           varchar(255) charset latin1 default 'maxwell' not null,
    heartbeat_at        bigint,
    last_heartbeat_read bigint,
    primary key (server_id, client_id)
);


drop table if exists `maxwell`.`schemas`;
create table `schemas`
(
    id                  bigint auto_increment primary key,
    binlog_file         varchar(255),
    binlog_position     int unsigned,
    last_heartbeat_read bigint            default 0,
    gtid_set            varchar(4096),
    base_schema_id      bigint,
    deltas              mediumtext,
    server_id           int unsigned,
    position_sha        char(40),
    charset             varchar(255),
    version             smallint unsigned default '0' not null,
    deleted             tinyint(1)        default 0   not null,
    constraint position_sha unique (position_sha)
);


drop table if exists `maxwell`.`tables`;
create table `tables`
(
    `id`          bigint auto_increment primary key,
    `schema_id`   bigint,
    `database_id` bigint,
    `name`        varchar(255),
    `charset`     varchar(255),
    `pk`          varchar(1024)
);

create index `database_id` on `tables` (`database_id`);
create index `schema_id`   on `tables` (`schema_id`);


truncate table `bootstrap`;        
truncate table `columns`;        
truncate table `databases`;        
truncate table `heartbeats`;        
truncate table `positions`;        
truncate table `schemas`;        
truncate table `tables`;
