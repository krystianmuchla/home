package com.github.krystianmuchla.home.infrastructure.http.core;

import java.util.HashMap;
import java.util.Map;

public class Controllers {
    private static final Map<String, Controller> CONTROLLERS = new HashMap<>();

    static {
        for (var controller : ControllerConfig.CONTROLLERS) {
            for (var path : controller.paths) {
                CONTROLLERS.put(path, controller);
            }
        }
    }

    public static Controller get(String path) {
        return CONTROLLERS.get(path);
    }
}
