package com.github.krystianmuchla.home.domain.id.password;

import com.github.krystianmuchla.home.application.util.StringService;
import com.github.krystianmuchla.home.domain.id.password.error.PasswordValidationError;
import com.github.krystianmuchla.home.domain.id.password.error.PasswordValidationException;

import java.util.HashSet;
import java.util.Set;

public class PasswordValidator {
    private static final int VALUE_MIN_LENGTH = 8;
    private static final int VALUE_MAX_LENGTH = 50;
    private static final Set<Character> VALUE_CHARACTERS = StringService.toSet(
        "abcdefghijklmnopqrstuvwxyz"
            + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "0123456789"
            + "`~!@#$%^&*()-_=+[{]};:'\"\\|,<.>/? "
    );

    private final Set<PasswordValidationError> errors = new HashSet<>();

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void validateValue(String value) {
        if (value == null) {
            errors.add(new PasswordValidationError.NullValue());
        } else {
            if (value.length() < VALUE_MIN_LENGTH) {
                errors.add(new PasswordValidationError.ValueBelowMinLength(VALUE_MIN_LENGTH));
            }
            if (value.length() > VALUE_MAX_LENGTH) {
                errors.add(new PasswordValidationError.ValueAboveMaxLength(VALUE_MAX_LENGTH));
            }
            for (var character : value.toCharArray()) {
                if (!VALUE_CHARACTERS.contains(character)) {
                    errors.add(new PasswordValidationError.ValueWrongFormat());
                    break;
                }
            }
        }
    }

    public static void validate(Password password) throws PasswordValidationException {
        var validator = new PasswordValidator();
        validator.validateValue(password.value);
        if (validator.hasErrors()) {
            throw new PasswordValidationException(validator.errors);
        }
    }
}
