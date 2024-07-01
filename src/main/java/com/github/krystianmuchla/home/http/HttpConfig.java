package com.github.krystianmuchla.home.http;

import com.github.krystianmuchla.home.Config;
import com.github.krystianmuchla.home.controller.FaviconController;
import com.github.krystianmuchla.home.controller.FontController;
import com.github.krystianmuchla.home.controller.HealthApiController;
import com.github.krystianmuchla.home.drive.DriveApiController;
import com.github.krystianmuchla.home.drive.DriveController;
import com.github.krystianmuchla.home.id.controller.*;
import com.github.krystianmuchla.home.note.NoteApiController;
import com.github.krystianmuchla.home.note.sync.NoteSyncApiController;

import java.util.List;

public class HttpConfig extends Config {
    public static final int PORT;
    public static final List<Controller> CONTROLLERS = List.of(
        new DriveApiController(),
        new DriveController(),
        new FaviconController(),
        new FontController(),
        new HealthApiController(),
        new InitSignUpApiController(),
        new NoteApiController(),
        new NoteSyncApiController(),
        new SignInApiController(),
        new SignInController(),
        new SignOutApiController(),
        new SignUpApiController(),
        new SignUpController()
    );

    static {
        final var port = resolve("http.port", "HOME_HTTP_PORT");
        PORT = Integer.parseInt(port);
    }
}
