--liquibase formatted sql

-- changeset escott:3
ALTER TABLE questions
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PENDING';
