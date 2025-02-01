package com.github.krystianmuchla.home.infrastructure.http.core;

import com.github.krystianmuchla.home.application.Config;
import com.github.krystianmuchla.home.application.util.MultiValueHashMap;
import com.github.krystianmuchla.home.application.util.MultiValueMap;
import com.github.krystianmuchla.home.infrastructure.http.drive.DriveController;
import com.github.krystianmuchla.home.infrastructure.http.drive.DriveUiController;
import com.github.krystianmuchla.home.infrastructure.http.drive.directory.DirectoryApiController;
import com.github.krystianmuchla.home.infrastructure.http.drive.file.FileApiController;
import com.github.krystianmuchla.home.infrastructure.http.id.*;
import com.github.krystianmuchla.home.infrastructure.http.note.sync.NoteSyncApiController;

import java.util.ArrayList;
import java.util.List;

public class ControllerConfig extends Config {
    public static final List<Controller> CONTROLLERS = new ArrayList<>();
    public static final String DEFAULT_PATH = DriveController.INSTANCE.getPath();
    public static final MultiValueMap<String, Method> OPTIONAL_USER_ROUTES = new MultiValueHashMap<>();
    public static final MultiValueMap<String, Method> NO_USER_ROUTES = new MultiValueHashMap<>();

    static {
        CONTROLLERS.add(DirectoryApiController.INSTANCE);
        CONTROLLERS.add(DriveController.INSTANCE);
        CONTROLLERS.add(DriveUiController.INSTANCE);
        CONTROLLERS.add(FileApiController.INSTANCE);
        CONTROLLERS.add(HealthApiController.INSTANCE);
        CONTROLLERS.add(NoteSyncApiController.INSTANCE);
        CONTROLLERS.add(ResourceController.INSTANCE);
        CONTROLLERS.add(RootController.INSTANCE);
        CONTROLLERS.add(SessionApiController.INSTANCE);
        CONTROLLERS.add(SignInController.INSTANCE);
        CONTROLLERS.add(SignUpController.INSTANCE);
        CONTROLLERS.add(UserApiController.INSTANCE);
        CONTROLLERS.add(UserInitApiController.INSTANCE);
        optionalUserRoute(HealthApiController.INSTANCE);
        optionalUserRoute(ResourceController.INSTANCE);
        optionalUserRoute(RootController.INSTANCE);
        noUserRoutes(SessionApiController.INSTANCE, Method.POST);
        noUserRoutes(SignInController.INSTANCE);
        noUserRoutes(SignUpController.INSTANCE);
        noUserRoutes(UserApiController.INSTANCE);
        noUserRoutes(UserInitApiController.INSTANCE);
    }

    private static void optionalUserRoute(Controller controller) {
        for (var path : controller.paths) {
            OPTIONAL_USER_ROUTES.addAll(path, Method.values());
        }
    }

    private static void noUserRoutes(Controller controller) {
        noUserRoutes(controller, Method.values());
    }

    private static void noUserRoutes(Controller controller, Method... methods) {
        for (var path : controller.paths) {
            NO_USER_ROUTES.addAll(path, methods);
        }
    }
}
