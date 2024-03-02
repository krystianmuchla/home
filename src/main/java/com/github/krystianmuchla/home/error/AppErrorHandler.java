package com.github.krystianmuchla.home.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Dispatcher;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ErrorHandler;

public class AppErrorHandler extends ErrorHandler {
    @Override
    public void handle(
        final String target,
        final Request baseRequest,
        final HttpServletRequest request,
        final HttpServletResponse response
    ) {
        Throwable throwable = (Throwable) request.getAttribute(Dispatcher.ERROR_EXCEPTION);
        if (throwable instanceof final AppError appException) {
            appException.accept(response);
        } else {
            response.setStatus(500);
        }
    }
}
