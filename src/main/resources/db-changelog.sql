-- liquibase formatted sql

-- changeset liquibase:1
CREATE TABLE note (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    title TINYTEXT NOT NULL,
    content TEXT NOT NULL,
    creation_time TIMESTAMP(3) NOT NULL,
    modification_time TIMESTAMP(3) NOT NULL
);

-- changeset liquibase:2
CREATE TABLE note_grave (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    creation_time TIMESTAMP(3) NOT NULL
);

-- changeset liquibase:3
CREATE TABLE user (
    id CHAR(36) PRIMARY KEY
);

-- changeset liquibase:4
CREATE TABLE access_data (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36),
    login VARCHAR(50),
    salt BINARY(32),
    secret BINARY(32),
    FOREIGN KEY (user_id) REFERENCES user(id)
);
