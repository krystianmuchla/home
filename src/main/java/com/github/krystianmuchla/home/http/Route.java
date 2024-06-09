package com.github.krystianmuchla.home.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Route {
    public Controller controller;
    public final Map<String, Route> children;

    public Route() {
        this.controller = null;
        this.children = new HashMap<>();
    }

    public Route(final Controller controller) {
        this.controller = controller;
        this.children = new HashMap<>();
    }

    public static Map<String, Route> routes(final List<Controller> controllers) {
        final Map<String, Route> routes = new HashMap<>();
        for (final var controller : controllers) {
            routes(routes, controller);
        }
        return routes;
    }

    private static void routes(Map<String, Route> routes, final Controller controller) {
        final var segments = controller.segments;
        for (int index = 0; index < segments.size(); index++) {
            final var last = index == segments.size() - 1;
            final var segment = segments.get(index);
            var route = routes.get(segment);
            if (route == null) {
                if (last) {
                    route = new Route(controller);
                } else {
                    route = new Route();
                }
                routes.put(segment, route);
            } else {
                if (last) {
                    route.controller = controller;
                }
            }
            routes = route.children;
        }
    }
}
