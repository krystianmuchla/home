package com.github.krystianmuchla.home;

import com.github.krystianmuchla.home.api.Controller;
import com.github.krystianmuchla.home.api.ResponseWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

public class HealthController extends Controller {
    public static final String PATH = "/api/health";

    @Override
    @SneakyThrows
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) {
        ResponseWriter.writeJson(response, "{}");
    }
}
