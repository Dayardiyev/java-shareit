drop table if exists users cascade;
drop table if exists items cascade;
drop table if exists bookings cascade;
drop table if exists comments cascade;
drop table if exists requests cascade;

create table if not exists users
(
    id    bigint generated always as identity primary key,
    name  varchar(255) not null,
    email varchar(512) not null unique
);

create table if not exists requests
(
    id          bigint generated always as identity primary key,
    description varchar(1000)                                  not null,
    created     timestamp without time zone                    not null,
    author_id   bigint references users (id) on delete cascade not null
);

create table if not exists items
(
    id          bigint generated always as identity primary key,
    name        varchar(255) not null,
    description varchar      not null,
    available   bool         not null,
    owner_id    bigint references users (id) on delete cascade,
    request_id  bigint references requests (id) on delete cascade
);

create table if not exists bookings
(
    id         bigint generated always as identity primary key,
    booker_id  bigint references users (id) on delete cascade,
    item_id    bigint references items (id) on delete cascade,
    start_time timestamp without time zone not null,
    end_time   timestamp without time zone not null,
    status     varchar(10)                 not null
);


create table if not exists comments
(
    id         bigint generated always as identity primary key,
    text       varchar(1000)                                  not null,
    created_at timestamp without time zone                    not null,
    author_id  bigint references users (id) on delete cascade not null,
    item_id    bigint references items (id) on delete cascade not null
);