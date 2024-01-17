package com.github.krystianmuchla.home;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppConfig extends Config {
    public static final int PORT;

    static {
        var port = resolve("port", "HOME_PORT");
        if (port == null) port = "80";
        PORT = Integer.parseInt(port);
    }
}
