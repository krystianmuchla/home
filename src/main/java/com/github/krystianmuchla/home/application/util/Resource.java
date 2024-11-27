package com.github.krystianmuchla.home.application.util;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;

public enum Resource {
    COMMON_STYLE("ui/style/common.css"),
    CONTEXT_MENU_SCRIPT("ui/script/context-menu.js"),
    CONTEXT_MENU_IMAGE("ui/image/context-menu.svg"),
    DRIVE_SCRIPT("ui/script/drive.js"),
    DRIVE_STYLE("ui/style/drive.css"),
    FONT("ui/font/Rubik-Regular.ttf"),
    ROUTER_SCRIPT("ui/script/router.js"),
    SIGN_IN_FORM_SCRIPT("ui/script/sign-in-form.js"),
    SIGN_IN_FORM_STYLE("ui/style/sign-in-form.css"),
    SIGN_UP_FORM_SCRIPT("ui/script/sign-up-form.js"),
    SIGN_UP_FORM_STYLE("ui/style/sign-up-form.css"),
    TOAST_SCRIPT("ui/script/toast.js"),
    ;

    public final String resourcePath;
    public final String urlPath;

    Resource(String resourcePath) {
        this.resourcePath = resourcePath;
        urlPath = "/" + resourcePath;
    }

    public InputStream asStream() {
        return inputStream(resourcePath);
    }

    public static Optional<Resource> findByUrlPath(String urlPath) {
        return Arrays.stream(Resource.values()).filter(resource -> resource.urlPath.equals(urlPath)).findFirst();
    }

    public static InputStream inputStream(String path) {
        return Resource.class.getClassLoader().getResourceAsStream(path);
    }
}
