--liquibase formatted sql
--changeset kpawelec:2
create table payment_order_status(
    id SERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    payment_status VARCHAR(32) NOT NULL,
    payment_url varchar not null
);
