package com.github.krystianmuchla.home;

public class AppConfig extends Config {
    public static final int PORT;

    static {
        var port = resolve("port", "HOME_PORT");
        if (port == null) port = "80";
        PORT = Integer.parseInt(port);
    }
}
