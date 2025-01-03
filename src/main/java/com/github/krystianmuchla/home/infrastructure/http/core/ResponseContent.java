package com.github.krystianmuchla.home.infrastructure.http.core;

public abstract sealed class ResponseContent<T> permits BytesResponseContent, InputStreamResponseContent {
    public final T value;

    protected ResponseContent(T value) {
        this.value = value;
    }
}
