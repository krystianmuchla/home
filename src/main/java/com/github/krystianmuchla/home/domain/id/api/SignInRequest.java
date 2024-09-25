package com.github.krystianmuchla.home.domain.id.api;

import com.github.krystianmuchla.home.application.exception.ValidationError;
import com.github.krystianmuchla.home.application.util.MultiValueHashMap;
import com.github.krystianmuchla.home.infrastructure.http.api.RequestBody;
import com.github.krystianmuchla.home.infrastructure.http.exception.BadRequestException;

public record SignInRequest(String login, String password) implements RequestBody {
    @Override
    public void validate() {
        var errors = new MultiValueHashMap<String, ValidationError>();
        if (login == null) {
            errors.add("login", ValidationError.nullValue());
        }
        if (password == null) {
            errors.add("password", ValidationError.nullValue());
        }
        if (!errors.isEmpty()) {
            throw new BadRequestException(errors);
        }
    }
}
