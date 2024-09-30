package com.github.krystianmuchla.home.domain.id.api;

import com.github.krystianmuchla.home.infrastructure.http.api.RequestBody;
import com.github.krystianmuchla.home.infrastructure.http.exception.BadRequestException;

import static com.github.krystianmuchla.home.domain.id.IdValidator.validateLogin;

public record SignInRequest(String login, String password) implements RequestBody {
    @Override
    public void validate() {
        var errors = validateLogin(login);
        if (!errors.isEmpty()) {
            throw new BadRequestException("login", errors);
        }
    }
}
