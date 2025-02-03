--liquibase formatted sql
--changeset kkosmala:8
ALTER TABLE concrete_ticket ADD COLUMN order_row_id INTEGER;
