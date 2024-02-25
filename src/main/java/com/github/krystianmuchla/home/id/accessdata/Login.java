package com.github.krystianmuchla.home.id.accessdata;

public class Login {
    public static class Validator {
        private static final int MIN_LENGTH = 1;
        private static final int MAX_LENGTH = 50;

        public static Error validate(final String login) {
            if (login == null) {
                return Error.TOO_SHORT;
            }
            if (login.length() < MIN_LENGTH) {
                return Error.TOO_SHORT;
            }
            if (login.length() > MAX_LENGTH) {
                return Error.TOO_LONG;
            }
            return null;
        }
    }

    public static enum Error {
        TOO_SHORT,
        TOO_LONG,
    }
}
