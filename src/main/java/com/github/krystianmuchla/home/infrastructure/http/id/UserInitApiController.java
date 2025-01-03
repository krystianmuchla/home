package com.github.krystianmuchla.home.infrastructure.http.id;

import com.github.krystianmuchla.home.domain.id.SignUpToken;
import com.github.krystianmuchla.home.infrastructure.http.core.Controller;
import com.github.krystianmuchla.home.infrastructure.http.core.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class UserInitApiController extends Controller {
    public static final UserInitApiController INSTANCE = new UserInitApiController();

    public UserInitApiController() {
        super("/api/users/init");
    }

    @Override
    protected void post(HttpExchange exchange) throws IOException {
        var success = SignUpToken.INSTANCE.generateAndLog();
        if (success) {
            new ResponseWriter(exchange).status(202).write();
        } else {
            new ResponseWriter(exchange).status(409).write();
        }
    }
}
