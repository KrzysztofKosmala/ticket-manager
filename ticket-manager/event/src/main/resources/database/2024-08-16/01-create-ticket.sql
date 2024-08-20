--liquibase formatted sql
--changeset kkosmala:5
-- Utworzenie tabeli ticket
CREATE TABLE ticket (
                              id SERIAL PRIMARY KEY,
                              event_occurrence_id INTEGER NOT NULL,
                              event_id INTEGER NOT NULL,
                              type VARCHAR(255) NOT NULL,
                              price NUMERIC(19, 2) NOT NULL,
                              amount INTEGER NOT NULL,
                              FOREIGN KEY (event_occurrence_id) REFERENCES event_occurrence(id) ON DELETE CASCADE,
                              FOREIGN KEY (event_id) REFERENCES event(id) ON DELETE CASCADE
);