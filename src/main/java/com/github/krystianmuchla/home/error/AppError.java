package com.github.krystianmuchla.home.error;

import jakarta.servlet.http.HttpServletResponse;

import java.util.function.Consumer;

public interface AppError {
    void handle(final HttpServletResponse response);
}
