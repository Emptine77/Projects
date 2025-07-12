CREATE SCHEMA IF NOT EXISTS users;

CREATE TABLE IF NOT EXISTS users.users
(
    id bigserial not null,
    email varchar(255) not null unique,
    password varchar(255) not null,
    first_name varchar(255),
    last_name varchar(255),
    role varchar(255) check (role in ('USER', 'ADMIN')) not null,
    status varchar(255) check (status in ('INACTIVE','ACTIVE','BANNED')) not null,
    created_at timestamp(6),
    updated_at timestamp(6),
    primary key (id)
);


