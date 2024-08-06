--liquibase formatted sql
--changeset kkosmala:1
ALTER TABLE event ADD COLUMN slug VARCHAR(255);

CREATE UNIQUE INDEX ui_event_slug ON event(slug);