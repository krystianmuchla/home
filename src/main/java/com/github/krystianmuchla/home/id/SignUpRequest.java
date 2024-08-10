package com.github.krystianmuchla.home.id;

import com.github.krystianmuchla.home.api.RequestBody;
import com.github.krystianmuchla.home.exception.ValidationError;
import com.github.krystianmuchla.home.exception.http.BadRequestException;
import com.github.krystianmuchla.home.id.accessdata.Login;
import com.github.krystianmuchla.home.id.accessdata.Password;
import com.github.krystianmuchla.home.util.MultiValueHashMap;

public record SignUpRequest(String name, String login, String password, String token) implements RequestBody {
    @Override
    public void validate() {
        var errors = new MultiValueHashMap<String, ValidationError>();
        if (name == null) {
            errors.add("name", ValidationError.nullValue());
        } else if (name.isBlank()) {
            errors.add("name", ValidationError.emptyValue());
        }
        var loginError = Login.Validator.validate(login);
        if (loginError != null) {
            errors.add("login", loginError);
        }
        var passwordErrors = Password.Validator.validate(password);
        if (!Password.Validator.validate(password).isEmpty()) {
            errors.addAll("password", passwordErrors);
        }
        if (token == null) {
            errors.add("token", ValidationError.nullValue());
        }
        if (!errors.isEmpty()) {
            throw new BadRequestException(errors);
        }
    }
}
