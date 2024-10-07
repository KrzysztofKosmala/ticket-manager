--liquibase formatted sql
--changeset kkosmala:2

-- Tworzenie tabeli Payment
CREATE TABLE payment (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         type VARCHAR(255)  NOT NULL,
                         default_payment BOOLEAN NOT NULL,
                         note TEXT
);

