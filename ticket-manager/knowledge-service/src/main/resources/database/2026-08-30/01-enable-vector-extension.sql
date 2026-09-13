--liquibase formatted sql

--changeset ticket:01-enable-vector-extension
CREATE EXTENSION IF NOT EXISTS vector;
