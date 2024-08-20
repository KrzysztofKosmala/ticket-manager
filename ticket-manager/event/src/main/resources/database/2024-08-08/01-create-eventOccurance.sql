--liquibase formatted sql
--changeset kkosmala:4


CREATE TABLE event_occurrence (
                                  id SERIAL PRIMARY KEY,
                                  event_id INTEGER NOT NULL,
                                  date DATE NOT NULL,
                                  time TIME NOT NULL,
                                  space_left INTEGER NOT NULL
);