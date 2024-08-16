--liquibase formatted sql
--changeset kkosmala:6
-- Utworzenie tabeli admin_ticket
CREATE TABLE admin_ticket (
                              id SERIAL PRIMARY KEY,
                              event_occurrence_id INTEGER NOT NULL,
                              event_id INTEGER NOT NULL,
                              type VARCHAR(255) NOT NULL,
                              price NUMERIC(19, 2) NOT NULL,
                              amount INTEGER NOT NULL,
                              FOREIGN KEY (event_occurrence_id) REFERENCES event_occurrence(id) ON DELETE CASCADE,
                              FOREIGN KEY (event_id) REFERENCES event(id) ON DELETE CASCADE
);