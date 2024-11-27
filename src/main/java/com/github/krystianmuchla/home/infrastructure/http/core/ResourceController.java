package com.github.krystianmuchla.home.infrastructure.http.core;

import com.github.krystianmuchla.home.application.util.Resource;
import com.github.krystianmuchla.home.infrastructure.http.core.exception.NotFoundException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ResourceController extends Controller {
    public static final ResourceController INSTANCE = new ResourceController();

    private final Map<String, Response> responses = new HashMap<>();

    public ResourceController() {
        super(
            Arrays.stream(Resource.values())
                .map(resource -> resource.urlPath)
                .collect(Collectors.toSet())
        );
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException {
        var path = exchange.getRequestURI().getPath();
        var response = getResponse(path);
        ResponseWriter.writeBytes(exchange, 200, response.contentType, response.content);
    }

    private Response getResponse(String path) throws IOException {
        var response = responses.get(path);
        if (response != null) {
            return response;
        }
        var resource = Resource.findByUrlPath(path).orElseThrow(NotFoundException::new);
        var contentType = URLConnection.guessContentTypeFromName(resource.resourcePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        try (var inputStream = resource.asStream()) {
            var content = inputStream.readAllBytes();
            responses.put(path, new Response(contentType, content));
        }
        return getResponse(path);
    }

    private record Response(String contentType, byte[] content) {
    }
}
