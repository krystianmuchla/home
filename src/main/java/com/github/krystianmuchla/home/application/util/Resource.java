package com.github.krystianmuchla.home.application.util;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public enum Resource {
    COMMON_STYLE("ui/style/common.css"),
    CONTEXT_MENU_SCRIPT("ui/script/context-menu.js"),
    CONTEXT_MENU_IMAGE("ui/image/context-menu.svg"),
    DRIVE_SCRIPT("ui/script/drive.js"),
    DRIVE_STYLE("ui/style/drive.css"),
    EXPLICIT_ROUTER_SCRIPT("ui/script/explicit-router.js"),
    FONT("ui/font/Rubik-Regular.ttf"),
    IMPLICIT_ROUTER_SCRIPT("ui/script/implicit-router.js"),
    SIGN_IN_FORM_SCRIPT("ui/script/sign-in-form.js"),
    SIGN_IN_FORM_STYLE("ui/style/sign-in-form.css"),
    SIGN_UP_FORM_SCRIPT("ui/script/sign-up-form.js"),
    SIGN_UP_FORM_STYLE("ui/style/sign-up-form.css"),
    TOAST_SCRIPT("ui/script/toast.js");

    private static final Map<String, Resource> RESOURCES =
        CollectionService.toMap(resource -> resource.urlPath, List.of(Resource.values()));

    public final String resourcePath;
    public final String urlPath;

    Resource(String resourcePath) {
        this.resourcePath = resourcePath;
        urlPath = "/" + resourcePath;
    }

    public InputStream asStream() {
        return inputStream(resourcePath);
    }

    public static Optional<Resource> getByUrlPath(String urlPath) {
        return Optional.ofNullable(RESOURCES.get(urlPath));
    }

    public static InputStream inputStream(String path) {
        return Resource.class.getClassLoader().getResourceAsStream(path);
    }
}
