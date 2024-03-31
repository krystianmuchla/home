package com.github.krystianmuchla.home.error.exception;

import com.github.krystianmuchla.home.api.ResponseWriter;
import com.github.krystianmuchla.home.error.AppError;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public class ConflictException extends RuntimeException implements AppError {
    private final String error;

    public ConflictException(final String error) {
        super("Conflict error: " + error);
        this.error = error;
    }

    @Override
    public void handle(HttpServletResponse response) {
        response.setStatus(409);
        ResponseWriter.writeJson(response, Map.of("error", error));
    }
}
