package com.github.krystianmuchla.home.infrastructure.http.core;

import java.io.InputStream;

public final class InputStreamResponseContent extends ResponseContent<InputStream> {
    public final Long length;

    public InputStreamResponseContent(Long length, InputStream content) {
        super(content);
        this.length = length;
    }
}
