package com.github.krystianmuchla.home.id.accessdata;

import com.github.krystianmuchla.home.exception.ValidationError;

public class Login {
    public static class Validator {
        private static final int MIN_LENGTH = 1;
        private static final int MAX_LENGTH = 50;

        public static ValidationError validate(final String login) {
            if (login == null) {
                return ValidationError.nullValue();
            }
            if (login.length() < MIN_LENGTH) {
                return ValidationError.belowMinLength(MIN_LENGTH);
            }
            if (login.length() > MAX_LENGTH) {
                return ValidationError.aboveMaxLength(MAX_LENGTH);
            }
            return null;
        }
    }
}
