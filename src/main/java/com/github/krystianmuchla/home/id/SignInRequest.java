package com.github.krystianmuchla.home.id;

import com.github.krystianmuchla.home.api.RequestBody;
import com.github.krystianmuchla.home.error.exception.validation.ValidationError;
import com.github.krystianmuchla.home.error.exception.validation.ValidationException;
import com.github.krystianmuchla.home.util.MultiValueHashMap;

public record SignInRequest(String login, String password) implements RequestBody {
    @Override
    public void validate() {
        final var errors = new MultiValueHashMap<String, ValidationError>();
        if (login == null) {
            errors.add("login", ValidationError.nullValue());
        }
        if (password == null) {
            errors.add("password", ValidationError.nullValue());
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
