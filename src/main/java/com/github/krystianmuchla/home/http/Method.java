package com.github.krystianmuchla.home.http;

import com.github.krystianmuchla.home.exception.http.MethodNotAllowedException;

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
