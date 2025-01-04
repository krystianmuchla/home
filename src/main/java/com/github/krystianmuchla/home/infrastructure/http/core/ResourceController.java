package com.github.krystianmuchla.home.infrastructure.http.core;

import com.github.krystianmuchla.home.application.util.Resource;
import com.github.krystianmuchla.home.infrastructure.http.core.error.NotFoundException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ResourceController extends Controller {
    public static final ResourceController INSTANCE = new ResourceController();
    private static final Set<Resource> CLIENT_CACHED_RESOURCES = Set.of(Resource.CONTEXT_MENU_IMAGE, Resource.FONT);
    private final Map<Resource, String> cachedContentTypes = new HashMap<>();
    private final Map<Resource, byte[]> cachedContents = new HashMap<>();

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
        var resource = Resource.getByUrlPath(path).orElseThrow(NotFoundException::new);
        var contentType = getAndSaveContentType(resource);
        if (CLIENT_CACHED_RESOURCES.contains(resource)) {
            var content = getContent(resource);
            new ResponseWriter(exchange)
                .cacheControl("public, max-age=86400, must-revalidate")
                .content(contentType, content)
                .write();
        } else {
            var content = getAndSaveContent(resource);
            new ResponseWriter(exchange)
                .content(contentType, content)
                .write();
        }
    }

    private String getContentType(Resource resource) {
        return HttpService.resolveContentType(resource.resourcePath);
    }

    private String getAndSaveContentType(Resource resource) {
        var contentType = cachedContentTypes.get(resource);
        if (contentType != null) {
            return contentType;
        }
        contentType = getContentType(resource);
        cachedContentTypes.put(resource, contentType);
        return contentType;
    }

    private byte[] getContent(Resource resource) throws IOException {
        try (var inputStream = resource.asStream()) {
            return inputStream.readAllBytes();
        }
    }

    private byte[] getAndSaveContent(Resource resource) throws IOException {
        var content = cachedContents.get(resource);
        if (content != null) {
            return content;
        }
        content = getContent(resource);
        cachedContents.put(resource, content);
        return content;
    }
}
