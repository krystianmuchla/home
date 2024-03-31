package com.github.krystianmuchla.home;

import org.testcontainers.containers.MySQLContainer;

public class AppContext {

    public static final String PORT = "80";
    public static final String HOST = "http://localhost:" + PORT;
    public static MySQLContainer<?> dbContainer;

    private static boolean initialized = false;

    public static void init() {
        if (!initialized) {
            dbContainer = new MySQLContainer<>("mysql:8.1.0");
            dbContainer.start();
            System.setProperty("port", PORT);
            System.setProperty("database.url", dbContainer.getJdbcUrl());
            System.setProperty("database.user", dbContainer.getUsername());
            System.setProperty("database.password", dbContainer.getPassword());
            System.setProperty("note-grave-cleaner.enabled", "false");
            App.main();
            initialized = true;
        }
    }
}
