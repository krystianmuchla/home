package com.github.krystianmuchla.home;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.MySQLContainer;

public abstract class AppTest {

    protected static final String APP_PORT = "80";
    protected static final String APP_HOST = "http://localhost:" + APP_PORT;
    protected static MySQLContainer<?> dbContainer;
    private static boolean initialized = false;

    @BeforeAll
    protected static void beforeAllTests() throws Exception {
        if (!initialized) {
            dbContainer = new MySQLContainer<>("mysql:8.1.0");
            dbContainer.start();
            System.setProperty("port", APP_PORT);
            System.setProperty("database.url", dbContainer.getJdbcUrl());
            System.setProperty("database.user", dbContainer.getUsername());
            System.setProperty("database.password", dbContainer.getPassword());
            System.setProperty("note-grave-cleaner.enabled", "false");
            App.main();
            initialized = true;
        }
    }
}
