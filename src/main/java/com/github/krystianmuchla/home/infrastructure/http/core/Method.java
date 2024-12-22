package com.github.krystianmuchla.home.infrastructure.http.core;

import com.github.krystianmuchla.home.infrastructure.http.core.error.MethodNotAllowedException;

public enum Method {
    DELETE, GET, POST, PUT;

    public static Method of(String method) {
        try {
            return valueOf(method);
        } catch (IllegalArgumentException exception) {
            throw new MethodNotAllowedException();
        }
    }
}
