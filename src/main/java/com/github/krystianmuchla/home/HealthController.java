package com.github.krystianmuchla.home;

import com.github.krystianmuchla.home.api.ResponseWriter;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class HealthController extends HttpServlet {
    public static final String PATH = "/api/health";

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        ResponseWriter.writeJson(response, "{}");
    }
}
