package com.github.krystianmuchla.home.id;

import com.github.krystianmuchla.home.api.RequestBody;
import com.github.krystianmuchla.home.exception.ValidationError;
import com.github.krystianmuchla.home.exception.http.BadRequestException;
import com.github.krystianmuchla.home.util.MultiValueHashMap;

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
