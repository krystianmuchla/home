package com.github.krystianmuchla.home.http;

import com.github.krystianmuchla.home.Config;
import com.github.krystianmuchla.home.controller.FaviconController;
import com.github.krystianmuchla.home.controller.FontController;
import com.github.krystianmuchla.home.controller.HealthApiController;
import com.github.krystianmuchla.home.controller.RootController;
import com.github.krystianmuchla.home.drive.controller.DriveApiController;
import com.github.krystianmuchla.home.drive.controller.DriveController;
import com.github.krystianmuchla.home.drive.controller.DriveUiController;
import com.github.krystianmuchla.home.exception.InternalException;
import com.github.krystianmuchla.home.id.controller.*;
import com.github.krystianmuchla.home.note.NoteApiController;
import com.github.krystianmuchla.home.note.sync.NoteSyncApiController;

import java.util.List;
import java.util.Set;

public class HttpConfig extends Config {
    public static final Integer PORT;
    public static final String DEFAULT_PATH = DriveController.PATH;
    public static final Set<String> ANONYMOUS_PATHS = Set.of(
        FaviconController.PATH,
        FontController.PATH,
        HealthApiController.PATH
    );
    public static final Set<String> NO_USER_PATHS = Set.of(
        InitSignUpApiController.PATH,
        SignInApiController.PATH,
        SignInController.PATH,
        SignUpApiController.PATH,
        SignUpController.PATH
    );
    public static final List<Controller> CONTROLLERS = List.of(
        new DriveApiController(),
        new DriveController(),
        new DriveUiController(),
        new FaviconController(),
        new FontController(),
        new HealthApiController(),
        new InitSignUpApiController(),
        new NoteApiController(),
        new NoteSyncApiController(),
        new RootController(),
        new SignInApiController(),
        new SignInController(),
        new SignOutApiController(),
        new SignUpApiController(),
        new SignUpController()
    );

    static {
        final var port = resolve("http.port", "HOME_HTTP_PORT");
        if (port == null) {
            throw new InternalException("Http port is not specified");
        }
        PORT = Integer.valueOf(port);
    }
}
