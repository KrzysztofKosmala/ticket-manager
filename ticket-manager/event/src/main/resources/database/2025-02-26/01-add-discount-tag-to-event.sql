--liquibase formatted sql
--changeset kkosmala:10
ALTER TABLE event ADD COLUMN discount_tag TEXT;