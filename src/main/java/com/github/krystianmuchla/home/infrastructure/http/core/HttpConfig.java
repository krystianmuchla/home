package com.github.krystianmuchla.home.infrastructure.http.core;

import com.github.krystianmuchla.home.application.Config;

public class HttpConfig extends Config {
    public static final Integer PORT;

    static {
        var port = resolve("http.port", "HOME_HTTP_PORT");
        if (port == null) {
            throw new IllegalStateException("Http port is not specified");
        }
        PORT = Integer.valueOf(port);
    }
}
