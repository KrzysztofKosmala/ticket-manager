--liquibase formatted sql
--changeset kkosmala:9
ALTER TABLE ticket ADD COLUMN old_price NUMERIC(19, 2);