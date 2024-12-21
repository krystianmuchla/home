package com.github.krystianmuchla.home.domain.id.password;

import com.github.krystianmuchla.home.domain.id.password.error.PasswordValidationException;

public class Password {
    public final String value;

    public Password(String value) throws PasswordValidationException {
        this.value = value;
        PasswordValidator.validate(this);
    }
}
