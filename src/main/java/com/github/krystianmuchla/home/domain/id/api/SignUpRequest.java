package com.github.krystianmuchla.home.domain.id.api;

import com.github.krystianmuchla.home.application.exception.ValidationError;
import com.github.krystianmuchla.home.application.util.MultiValueHashMap;
import com.github.krystianmuchla.home.infrastructure.http.api.RequestBody;
import com.github.krystianmuchla.home.infrastructure.http.exception.BadRequestException;

import static com.github.krystianmuchla.home.domain.id.IdValidator.*;

public record SignUpRequest(String name, String login, String password, String token) implements RequestBody {
    @Override
    public void validate() {
        var errors = new MultiValueHashMap<String, ValidationError>();
        errors.addAll("name", validateUserName(name));
        errors.addAll("login", validateLogin(login));
        errors.addAll("password", validatePassword(password));
        if (!errors.isEmpty()) {
            throw new BadRequestException(errors);
        }
    }
}
