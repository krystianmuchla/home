package com.github.krystianmuchla.home.infrastructure.http.core;

import java.net.URLConnection;

public class HttpService {
    public static String resolveContentType(String name) {
        var contentType = URLConnection.guessContentTypeFromName(name);
        if (contentType == null) {
            return "application/octet-stream";
        }
        return contentType;
    }
}
