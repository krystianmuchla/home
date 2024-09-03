CREATE TABLE user (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    creation_time TIMESTAMP(3) NOT NULL,
    modification_time TIMESTAMP(3) NOT NULL
);

CREATE TABLE access_data (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36),
    login VARCHAR(50),
    salt BINARY(32),
    secret BINARY(32),
    creation_time TIMESTAMP(3) NOT NULL,
    modification_time TIMESTAMP(3) NOT NULL,
    CONSTRAINT fk_access_data_user_id FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE note (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    creation_time TIMESTAMP(3) NOT NULL,
    modification_time TIMESTAMP(3) NOT NULL,
    CONSTRAINT fk_note_user_id FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE removed_note (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    creation_time TIMESTAMP(3) NOT NULL,
    modification_time TIMESTAMP(3) NOT NULL,
    CONSTRAINT fk_removed_note_user_id FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE directory (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL,
    parent_id CHAR(36),
    name VARCHAR(255) NOT NULL,
    creation_time TIMESTAMP(3) NOT NULL,
    modification_time TIMESTAMP(3) NOT NULL,
    CONSTRAINT fk_directory_user_id FOREIGN KEY (user_id) REFERENCES user(id),
    CONSTRAINT fk_directory_parent_id FOREIGN KEY (parent_id) REFERENCES directory(id)
);

CREATE TABLE file (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL,
    directory_id CHAR(36),
    name VARCHAR(255) NOT NULL,
    creation_time TIMESTAMP(3) NOT NULL,
    modification_time TIMESTAMP(3) NOT NULL,
    CONSTRAINT fk_file_user_id FOREIGN KEY (user_id) REFERENCES user(id),
    CONSTRAINT fk_file_directory_id FOREIGN KEY (directory_id) REFERENCES directory(id)
);
