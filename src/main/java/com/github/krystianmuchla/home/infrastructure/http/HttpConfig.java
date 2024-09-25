package com.github.krystianmuchla.home.infrastructure.http;

import com.github.krystianmuchla.home.application.Config;
import com.github.krystianmuchla.home.application.exception.InternalException;
import com.github.krystianmuchla.home.application.util.MultiValueHashMap;
import com.github.krystianmuchla.home.application.util.MultiValueMap;
import com.github.krystianmuchla.home.domain.drive.api.DriveApiController;
import com.github.krystianmuchla.home.domain.drive.api.DriveController;
import com.github.krystianmuchla.home.domain.drive.api.DriveUiController;
import com.github.krystianmuchla.home.domain.id.api.SignInController;
import com.github.krystianmuchla.home.domain.id.api.SignUpController;
import com.github.krystianmuchla.home.domain.id.session.SessionApiController;
import com.github.krystianmuchla.home.domain.id.user.UserApiController;
import com.github.krystianmuchla.home.domain.id.user.UserInitApiController;
import com.github.krystianmuchla.home.domain.note.sync.api.NoteSyncApiController;
import com.github.krystianmuchla.home.infrastructure.http.api.FaviconController;
import com.github.krystianmuchla.home.infrastructure.http.api.FontController;
import com.github.krystianmuchla.home.infrastructure.http.api.HealthApiController;
import com.github.krystianmuchla.home.infrastructure.http.api.RootController;

import java.util.List;

public class HttpConfig extends Config {
    public static final Integer PORT;
    public static final String DEFAULT_PATH = DriveController.PATH;
    public static final MultiValueMap<String, Method> OPTIONAL_USER_ROUTES;
    public static final MultiValueMap<String, Method> NO_USER_ROUTES;
    public static final List<Controller> CONTROLLERS = List.of(
        new DriveApiController(),
        new DriveController(),
        new DriveUiController(),
        new FaviconController(),
        new FontController(),
        new HealthApiController(),
        new NoteSyncApiController(),
        new RootController(),
        new SessionApiController(),
        new SignInController(),
        new SignUpController(),
        new UserApiController(),
        new UserInitApiController()
    );

    static {
        var port = resolve("http.port", "HOME_HTTP_PORT");
        if (port == null) {
            throw new InternalException("Http port is not specified");
        }
        PORT = Integer.valueOf(port);
        OPTIONAL_USER_ROUTES = new MultiValueHashMap<>();
        OPTIONAL_USER_ROUTES.addAll(FaviconController.PATH, Method.values());
        OPTIONAL_USER_ROUTES.addAll(FontController.PATH, Method.values());
        OPTIONAL_USER_ROUTES.addAll(HealthApiController.PATH, Method.values());
        OPTIONAL_USER_ROUTES.addAll(RootController.PATH, Method.values());
        NO_USER_ROUTES = new MultiValueHashMap<>();
        NO_USER_ROUTES.addAll(SessionApiController.PATH, Method.POST);
        NO_USER_ROUTES.addAll(SignInController.PATH, Method.values());
        NO_USER_ROUTES.addAll(SignUpController.PATH, Method.values());
        NO_USER_ROUTES.addAll(UserApiController.PATH, Method.values());
        NO_USER_ROUTES.addAll(UserInitApiController.PATH, Method.values());
    }
}
