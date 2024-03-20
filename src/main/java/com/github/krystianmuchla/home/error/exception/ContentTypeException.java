package com.github.krystianmuchla.home.error.exception;

import com.github.krystianmuchla.home.error.AppError;
import jakarta.servlet.http.HttpServletResponse;

public class ContentTypeException extends RuntimeException implements AppError {
    public ContentTypeException() {
        super("Unsupported content type");
    }

    @Override
    public void handle(HttpServletResponse response) {
        response.setStatus(415);
    }
}
