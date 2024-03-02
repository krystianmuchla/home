package com.github.krystianmuchla.home.error;

import jakarta.servlet.http.HttpServletResponse;

import java.util.function.Consumer;

public interface AppError extends Consumer<HttpServletResponse> {
}
