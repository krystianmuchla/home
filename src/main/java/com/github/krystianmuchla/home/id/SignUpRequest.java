package com.github.krystianmuchla.home.id;

import com.github.krystianmuchla.home.api.RequestBody;
import com.github.krystianmuchla.home.id.accessdata.Login;
import com.github.krystianmuchla.home.id.accessdata.Password;

public record SignUpRequest(String login, String password) implements RequestBody {
    @Override
    public void validate() {
        if (Login.Validator.validate(login) != null) {
            throw new IllegalArgumentException();
        }
        if (Password.Validator.validate(password).size() > 0) {
            throw new IllegalArgumentException();
        }
    }
}
