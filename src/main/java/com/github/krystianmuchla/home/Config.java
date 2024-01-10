package com.github.krystianmuchla.home;

public abstract class Config {
    protected static String resolve(final String systemProperty, final String environmentVariable) {
        final var configuration = System.getProperty(systemProperty);
        if (configuration != null) return configuration;
        return System.getenv(environmentVariable);
    }
}
