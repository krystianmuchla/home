package com.github.krystianmuchla.home.domain.id;

import com.github.krystianmuchla.home.application.exception.ValidationError;
import com.github.krystianmuchla.home.application.exception.Validator;
import com.github.krystianmuchla.home.application.util.CollectionService;
import com.github.krystianmuchla.home.application.util.StringService;
import com.github.krystianmuchla.home.domain.id.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IdValidator extends Validator {
    private static final int LOGIN_MAX_LENGTH = 50;
    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 50;
    private static final List<String> PASSWORD_REQUIRED_LETTERS_GROUPS = List.of(
        "abcdefghijklmnopqrstuvwxyz",
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
        "0123456789",
        "`~!@#$%^&*()-_=+[{]};:'\"\\|,<.>/? "
    );
    private static final String PASSWORD_ALLOWED_LETTERS = CollectionService.join(PASSWORD_REQUIRED_LETTERS_GROUPS);
    private static final int SALT_LENGTH = 32;
    private static final int SECRET_LENGTH = 32;
    private static final int USER_NAME_MAX_LENGTH = 100;

    public static List<ValidationError> validateAccessDataId(UUID accessDataId) {
        return validateUuid(accessDataId);
    }

    public static List<ValidationError> validateLogin(String login) {
        if (login == null) {
            return List.of(ValidationError.nullValue());
        }
        if (login.isEmpty()) {
            return List.of(ValidationError.emptyValue());
        }
        if (login.length() > LOGIN_MAX_LENGTH) {
            return List.of(ValidationError.aboveMaxLength(LOGIN_MAX_LENGTH));
        }
        return List.of();
    }

    public static List<ValidationError> validatePassword(String password) {
        if (password == null) {
            return List.of(ValidationError.nullValue());
        }
        if (password.isEmpty()) {
            return List.of(ValidationError.emptyValue());
        }
        var errors = new ArrayList<ValidationError>();
        if (password.length() < PASSWORD_MIN_LENGTH) {
            errors.add(ValidationError.belowMinLength(PASSWORD_MIN_LENGTH));
        } else if (password.length() > PASSWORD_MAX_LENGTH) {
            errors.add(ValidationError.aboveMaxLength(PASSWORD_MAX_LENGTH));
        }
        for (var requiredLettersGroup : PASSWORD_REQUIRED_LETTERS_GROUPS) {
            if (!StringService.containsAny(password, requiredLettersGroup.toCharArray())) {
                errors.add(ValidationError.wrongFormat());
                return errors;
            }
        }
        if (!StringService.containsOnly(password, PASSWORD_ALLOWED_LETTERS.toCharArray())) {
            errors.add(ValidationError.wrongFormat());
        }
        return errors;
    }

    public static List<ValidationError> validateSalt(byte[] salt) {
        if (salt == null) {
            return List.of(ValidationError.nullValue());
        }
        if (salt.length < SALT_LENGTH) {
            return List.of(ValidationError.belowMinLength(SALT_LENGTH));
        }
        if (salt.length > SALT_LENGTH) {
            return List.of(ValidationError.aboveMaxLength(SALT_LENGTH));
        }
        return List.of();
    }

    public static List<ValidationError> validateSecret(byte[] secret) {
        if (secret == null) {
            return List.of(ValidationError.nullValue());
        }
        if (secret.length < SECRET_LENGTH) {
            return List.of(ValidationError.belowMinLength(SECRET_LENGTH));
        }
        if (secret.length > SECRET_LENGTH) {
            return List.of(ValidationError.aboveMaxLength(SECRET_LENGTH));
        }
        return List.of();
    }

    public static List<ValidationError> validateUser(User user) {
        if (user == null) {
            return List.of(ValidationError.nullValue());
        }
        return List.of();
    }

    public static List<ValidationError> validateUserId(UUID userId) {
        return validateUuid(userId);
    }

    public static List<ValidationError> validateUserName(String userName) {
        if (userName == null) {
            return List.of(ValidationError.nullValue());
        }
        if (userName.isEmpty()) {
            return List.of(ValidationError.emptyValue());
        }
        if (userName.length() > USER_NAME_MAX_LENGTH) {
            return List.of(ValidationError.aboveMaxLength(USER_NAME_MAX_LENGTH));
        }
        return List.of();
    }
}
