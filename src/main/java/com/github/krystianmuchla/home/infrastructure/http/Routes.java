package com.github.krystianmuchla.home.infrastructure.http;

import com.github.krystianmuchla.home.application.exception.InternalException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Routes {
    private Controller controller;
    private final Map<String, Routes> children;

    public Routes() {
        this.controller = null;
        this.children = new HashMap<>();
    }

    public Routes(List<Controller> controllers) {
        this();
        controllers.forEach(controller -> addController(this, controller));
    }

    public Controller findController(String path) {
        var routes = this;
        for (var segment : Segment.segments(path)) {
            routes = routes.children.get(segment);
            if (routes == null) {
                return null;
            }
        }
        return routes.controller;
    }

    private static void addController(Routes routes, Controller controller) {
        var segments = new LinkedList<>(controller.segments);
        while (!segments.isEmpty()) {
            var segment = segments.removeFirst();
            routes = routes.children.computeIfAbsent(segment, s -> new Routes());
        }
        if (routes.controller != null) {
            var path = "/" + String.join("/", controller.segments);
            throw new InternalException("Controller with path '" + path + "' already exists");
        }
        routes.controller = controller;
    }
}
