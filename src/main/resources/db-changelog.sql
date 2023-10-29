-- liquibase formatted sql

-- changeset liquibase:1
CREATE TABLE note (id BINARY(16) PRIMARY KEY, title TINYTEXT NOT NULL, content TEXT NOT NULL);
