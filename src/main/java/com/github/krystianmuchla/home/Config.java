package com.github.krystianmuchla.home;

public class Config {
    protected static String resolve(String systemProperty, String environmentVariable) {
        var configuration = System.getProperty(systemProperty);
        if (configuration != null) {
            return configuration;
        }
        return System.getenv(environmentVariable);
    }
}
