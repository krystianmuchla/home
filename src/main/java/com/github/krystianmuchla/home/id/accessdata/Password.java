package com.github.krystianmuchla.home.id.accessdata;

import java.util.ArrayList;
import java.util.List;

public class Password {
    public static class Validator {
        private static final int MIN_LENGTH = 8;
        private static final int MAX_LENGTH = 50;
        private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
        private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        private static final String DIGIT = "0123456789";
        private static final String SPECIAL = "`~!@#$%^&*()-_=+[{]};:'\"\\|,<.>/? ";
        private static final String ALL = LOWER_CASE + UPPER_CASE + DIGIT + SPECIAL;

        public static List<Error> validate(final String password) {
            final var errors = new ArrayList<Error>();
            if (password == null) {
                errors.add(Error.TOO_SHORT);
                errors.add(Error.NO_LOWER_CASE);
                errors.add(Error.NO_UPPER_CASE);
                errors.add(Error.NO_DIGIT);
                errors.add(Error.NO_SPECIAL);
                return errors;
            }
            if (password.length() < MIN_LENGTH) {
                errors.add(Error.TOO_SHORT);
            }
            if (password.length() > MAX_LENGTH) {
                errors.add(Error.TOO_LONG);
            }
            if (!containsAny(password, LOWER_CASE.toCharArray())) {
                errors.add(Error.NO_LOWER_CASE);
            }
            if (!containsAny(password, UPPER_CASE.toCharArray())) {
                errors.add(Error.NO_UPPER_CASE);
            }
            if (!containsAny(password, DIGIT.toCharArray())) {
                errors.add(Error.NO_DIGIT);
            }
            if (!containsAny(password, SPECIAL.toCharArray())) {
                errors.add(Error.NO_SPECIAL);
            }
            if (!containsOnly(password, ALL.toCharArray())) {
                errors.add(Error.INVALID_CHARACTER);
            }
            return errors;
        }

        private static boolean containsAny(final String password, final char[] chars) {
            final char[] passwordCharArray = password.toCharArray();
            for (final char character : chars) {
                if (contains(passwordCharArray, character)) {
                    return true;
                }
            }
            return false;
        }

        private static boolean containsOnly(final String password, final char[] chars) {
            for (final char character : password.toCharArray()) {
                if (!contains(chars, character)) {
                    return false;
                }
            }
            return true;
        }

        private static boolean contains(final char[] charArray, final char character) {
            if (charArray.length == 0) {
                return false;
            }
            int index = 0;
            for (; index < charArray.length; index++) {
                if (charArray[index] == character) {
                    break;
                }
            }
            return index != charArray.length;
        }
    }

    public static enum Error {
        TOO_SHORT,
        TOO_LONG,
        NO_LOWER_CASE,
        NO_UPPER_CASE,
        NO_DIGIT,
        NO_SPECIAL,
        INVALID_CHARACTER;
    }
}
