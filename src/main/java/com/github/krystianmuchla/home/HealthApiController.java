package com.github.krystianmuchla.home;

import com.github.krystianmuchla.home.api.Controller;
import com.github.krystianmuchla.home.api.ResponseWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HealthApiController extends Controller {
    public static final String PATH = "/api/health";

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) {
        ResponseWriter.writeJson(response, "{}");
    }
}
