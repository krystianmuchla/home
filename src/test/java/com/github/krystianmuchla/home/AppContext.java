package com.github.krystianmuchla.home;

public class AppContext {

    public static final String PORT = "8081";
    public static final String HOST = "http://localhost:" + PORT;

    private static boolean initialized = false;

    public static void init() {
        if (!initialized) {
            System.setProperty("http.port", PORT);
            System.setProperty("database.url", "jdbc:sqlite::memory:");
            App.main();
            initialized = true;
        }
    }
}
