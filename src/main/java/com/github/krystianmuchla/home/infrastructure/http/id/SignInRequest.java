package com.github.krystianmuchla.home.infrastructure.http.id;

import com.github.krystianmuchla.home.infrastructure.http.core.RequestBody;
import com.github.krystianmuchla.home.infrastructure.http.core.exception.BadRequestException;

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
