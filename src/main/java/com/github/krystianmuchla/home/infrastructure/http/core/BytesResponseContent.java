package com.github.krystianmuchla.home.infrastructure.http.core;

public final class BytesResponseContent extends ResponseContent<byte[]> {
    public BytesResponseContent(byte[] content) {
        super(content);
    }
}
