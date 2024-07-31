--liquibase formatted sql
--changeset kkosmala:1
CREATE TABLE event (
                       id SERIAL PRIMARY KEY,
                       capacity INTEGER NOT NULL,
                       title TEXT NOT NULL,
                       description TEXT NOT NULL
);