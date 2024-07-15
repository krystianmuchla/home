CREATE TABLE directory (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    parent_id CHAR(36),
    path TEXT NOT NULL
);

CREATE TABLE file (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    directory_id CHAR(36),
    path TEXT NOT NULL
);

ALTER TABLE user ADD COLUMN name VARCHAR(100) NOT NULL;
