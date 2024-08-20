--liquibase formatted sql
--changeset kkosmala:2
-- Dodanie nowej kolumny category_id
ALTER TABLE event ADD COLUMN category_id BIGINT;

-- Dodanie klucza obcego do category_id
ALTER TABLE event ADD CONSTRAINT fk_event_category_id FOREIGN KEY (category_id) REFERENCES category(id);
