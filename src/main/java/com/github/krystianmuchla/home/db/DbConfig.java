package com.github.krystianmuchla.home.db;

import com.github.krystianmuchla.home.Config;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DbConfig extends Config {
    public static final String URL;
    public static final String USER;
    public static final String PASSWORD;

    static {
        final var url = resolve("database.url", "HOME_DATABASE_URL");
        if (url == null) throw new IllegalStateException("Database url is not specified");
        URL = url;
        final var user = resolve("database.user", "HOME_DATABASE_USER");
        if (user == null) throw new IllegalStateException("Database user is not specified");
        USER = user;
        final var password = resolve("database.password", "HOME_DATABASE_PASSWORD");
        if (password == null) throw new IllegalStateException("Database password is not specified");
        PASSWORD = password;
    }
}
