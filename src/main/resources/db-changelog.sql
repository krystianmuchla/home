-- liquibase formatted sql

-- changeset liquibase:1
CREATE TABLE note (id VARCHAR(36) PRIMARY KEY, title TINYTEXT NOT NULL, content TEXT NOT NULL, creation_time TIMESTAMP(3) NOT NULL, modification_time TIMESTAMP(3) NOT NULL);
CREATE TABLE note_sync (id INT PRIMARY KEY, sync_id VARCHAR(36) NOT NULL);
INSERT INTO note_sync VALUES (1, UUID());
