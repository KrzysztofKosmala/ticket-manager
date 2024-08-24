--liquibase formatted sql
--changeset kkosmala:1
CREATE TABLE cart (
                       id SERIAL PRIMARY KEY,
                       created date NOT NULL
);