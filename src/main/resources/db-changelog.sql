-- liquibase formatted sql

-- changeset liquibase:1
CREATE TABLE note (id VARCHAR(36) PRIMARY KEY, title TINYTEXT NOT NULL, content TEXT NOT NULL, creation_time TIMESTAMP(3) NOT NULL, modification_time TIMESTAMP(3) NOT NULL);
CREATE TABLE note_grave (id VARCHAR(36) PRIMARY KEY, creation_time TIMESTAMP(3) NOT NULL);
