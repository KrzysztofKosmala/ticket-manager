--liquibase formatted sql
--changeset kpawelec:1
CREATE TABLE ticket (
                        id SERIAL PRIMARY KEY,
                        eventId INTEGER NOT NULL,
                        type TEXT NOT NULL,
                        price DECIMAL NOT NULL,
                        amount INT NOT NULL
);
