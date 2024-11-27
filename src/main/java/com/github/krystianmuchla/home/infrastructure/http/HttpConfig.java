package com.github.krystianmuchla.home.infrastructure.http;

import com.github.krystianmuchla.home.application.Config;
import com.github.krystianmuchla.home.application.exception.InternalException;

public class HttpConfig extends Config {
    public static final Integer PORT;

    static {
        var port = resolve("http.port", "HOME_HTTP_PORT");
        if (port == null) {
            throw new InternalException("Http port is not specified");
        }
        PORT = Integer.valueOf(port);
    }
}
