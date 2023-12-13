package com.github.krystianmuchla.home;

import org.testcontainers.containers.MySQLContainer;

public class DatabaseContainer extends MySQLContainer<DatabaseContainer> {
    public DatabaseContainer() {
        super("mysql:8.1.0");
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("HOME_DATABASE_URL", getJdbcUrl());
        System.setProperty("HOME_DATABASE_USER", getUsername());
        System.setProperty("HOME_DATABASE_PASSWORD", getPassword());
    }
}
