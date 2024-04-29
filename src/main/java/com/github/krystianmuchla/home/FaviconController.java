package com.github.krystianmuchla.home;

import com.github.krystianmuchla.home.api.Controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FaviconController extends Controller {
    public static final String PATH = "/favicon.ico";

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) {
    }
}
