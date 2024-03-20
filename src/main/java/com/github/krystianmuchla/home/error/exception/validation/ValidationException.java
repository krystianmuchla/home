package com.github.krystianmuchla.home.error.exception.validation;

import com.github.krystianmuchla.home.api.ResponseWriter;
import com.github.krystianmuchla.home.error.AppError;
import com.github.krystianmuchla.home.util.MultiValueHashMap;
import com.github.krystianmuchla.home.util.MultiValueMap;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import java.util.Map;

public class ValidationException extends RuntimeException implements AppError {
    private final MultiValueMap<String, ValidationError> errors;

    public ValidationException(final String parameter, ValidationError error) {
        this(MultiValueHashMap.of(parameter, error));
    }

    public ValidationException(final MultiValueMap<String, ValidationError> errors) {
        super("Invalid input parameters: " + String.join(",", errors.keySet()));
        this.errors = errors;
    }

    @Override
    @SneakyThrows
    public void handle(final HttpServletResponse response) {
        response.setStatus(400);
        ResponseWriter.writeJson(response, Map.of("invalidParameters", errors));
    }
}
