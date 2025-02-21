package com.github.krystianmuchla.home.domain.id.password;

import com.github.krystianmuchla.home.application.util.StringService;
import com.github.krystianmuchla.home.domain.core.error.Validator;
import com.github.krystianmuchla.home.domain.id.password.error.PasswordValidationError;
import com.github.krystianmuchla.home.domain.id.password.error.PasswordValidationException;

import java.util.Set;

public class PasswordValidator extends Validator<PasswordValidationError, PasswordValidationException> {
    private static final int VALUE_MIN_LENGTH = 8;
    private static final int VALUE_MAX_LENGTH = 50;
    private static final Set<Character> VALUE_CHARACTERS = StringService.toSet(
        "abcdefghijklmnopqrstuvwxyz"
            + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "0123456789"
            + "`~!@#$%^&*()-_=+[{]};:'\"\\|,<.>/? "
    );

    private final Password password;

    public PasswordValidator(Password password) {
        this.password = password;
    }

    // todo fix
    public PasswordValidator checkValue() {
        if (password.value == null) {
            errors.add(new PasswordValidationError.NullValue());
        } else {
            if (password.value.length() < VALUE_MIN_LENGTH) {
                errors.add(new PasswordValidationError.ValueBelowMinLength(VALUE_MIN_LENGTH));
            }
            if (password.value.length() > VALUE_MAX_LENGTH) {
                errors.add(new PasswordValidationError.ValueAboveMaxLength(VALUE_MAX_LENGTH));
            }
            for (var character : password.value.toCharArray()) {
                if (!VALUE_CHARACTERS.contains(character)) {
                    errors.add(new PasswordValidationError.ValueWrongFormat());
                    break;
                }
            }
        }
        return this;
    }

    @Override
    public void validate() throws PasswordValidationException {
        if (hasErrors()) {
            throw new PasswordValidationException(errors);
        }
    }

    public static void validate(Password password) throws PasswordValidationException {
        new PasswordValidator(password)
            .checkValue()
            .validate();
    }
}
