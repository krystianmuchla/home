package com.github.krystianmuchla.home.error.exception;

import com.github.krystianmuchla.home.error.AppError;
import jakarta.servlet.http.HttpServletResponse;

public class MissingResourceException extends RuntimeException implements AppError {
    public MissingResourceException() {
        super("Not found");
    }

    @Override
    public void handle(HttpServletResponse response) {
        response.setStatus(404);
    }
}
