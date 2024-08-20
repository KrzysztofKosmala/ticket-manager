--liquibase formatted sql
--changeset kkosmala:3
-- Wstawienie nowego rekordu do tabeli category
INSERT INTO category (id, name, description, slug) VALUES (1, 'unknown', '', 'unknown');

-- Zaktualizowanie wszystkich rekordów w tabeli product
UPDATE event SET category_id = 1;

-- Zmodyfikowanie kolumny category_id, aby była NOT NULL
ALTER TABLE event ALTER COLUMN category_id SET NOT NULL;