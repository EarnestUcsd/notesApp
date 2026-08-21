--liquibase formatted sql

-- changeset escott:4
ALTER TABLE questions
    ADD COLUMN answer TEXT;
