package com.github.krystianmuchla.home.infrastructure.persistence.core;

import com.github.krystianmuchla.home.application.Config;

public class ConnectionConfig extends Config {
    public static final String URL;

    static {
        var url = resolve("database.url", "HOME_DATABASE_URL");
        if (url == null) {
            throw new IllegalStateException("Database url is not specified");
        }
        URL = url;
    }
}
