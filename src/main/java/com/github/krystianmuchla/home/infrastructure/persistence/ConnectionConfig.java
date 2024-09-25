package com.github.krystianmuchla.home.infrastructure.persistence;

import com.github.krystianmuchla.home.application.Config;
import com.github.krystianmuchla.home.application.exception.InternalException;

public class ConnectionConfig extends Config {
    public static final String URL;

    static {
        var url = resolve("database.url", "HOME_DATABASE_URL");
        if (url == null) {
            throw new InternalException("Database url is not specified");
        }
        URL = url;
    }
}
