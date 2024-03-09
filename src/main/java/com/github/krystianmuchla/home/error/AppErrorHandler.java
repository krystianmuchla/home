package com.github.krystianmuchla.home.error;

import com.github.krystianmuchla.home.error.exception.TransactionException;
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
        handle(response, throwable);
    }

    @Override
    public boolean errorPageForMethod(final String method) {
        return true;
    }

    private void handle(final HttpServletResponse response, final Throwable throwable) {
        switch (throwable) {
            case AppError appError -> appError.accept(response);
            case TransactionException transactionException -> {
                final Throwable cause = transactionException.getCause();
                if (cause != null) {
                    handle(response, cause);
                } else {
                    response.setStatus(500);
                }
            }
            default -> response.setStatus(500);
        }
    }
}
