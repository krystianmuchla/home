package com.github.krystianmuchla.home.db;

import com.github.krystianmuchla.home.Config;
import com.github.krystianmuchla.home.exception.InternalException;

public class ConnectionConfig extends Config {
    public static final String URL;
    public static final String USER;
    public static final String PASSWORD;
    public static final int POOL_SIZE = 2;

    static {
        final var url = resolve("database.url", "HOME_DATABASE_URL");
        if (url == null) {
            throw new InternalException("Database url is not specified");
        }
        URL = url;
        final var user = resolve("database.user", "HOME_DATABASE_USER");
        if (user == null) {
            throw new InternalException("Database user is not specified");
        }
        USER = user;
        final var password = resolve("database.password", "HOME_DATABASE_PASSWORD");
        if (password == null) {
            throw new InternalException("Database password is not specified");
        }
        PASSWORD = password;
    }
}
