package com.github.krystianmuchla.skyr;

import org.testcontainers.containers.MySQLContainer;

public class DatabaseContainer extends MySQLContainer<DatabaseContainer> {
    public DatabaseContainer() {
        super("mysql:8.1.0");
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("SKYR_DATABASE_URL", getJdbcUrl());
        System.setProperty("SKYR_DATABASE_USER", getUsername());
        System.setProperty("SKYR_DATABASE_PASSWORD", getPassword());
    }
}
