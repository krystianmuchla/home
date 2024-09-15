package com.github.krystianmuchla.home.db;

import com.github.krystianmuchla.home.Config;
import com.github.krystianmuchla.home.exception.InternalException;

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
