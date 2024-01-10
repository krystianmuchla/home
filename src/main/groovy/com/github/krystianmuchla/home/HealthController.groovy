package com.github.krystianmuchla.home

import com.github.krystianmuchla.home.api.ResponseWriter
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

class HealthController extends HttpServlet {
    static final PATH = '/api/health'

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        ResponseWriter.writeJson(response, '{}')
    }
}
