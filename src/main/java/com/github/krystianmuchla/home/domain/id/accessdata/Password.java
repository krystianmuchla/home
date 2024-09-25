package com.github.krystianmuchla.home.domain.id.accessdata;

import com.github.krystianmuchla.home.application.exception.ValidationError;

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

        public static List<ValidationError> validate(String password) {
            var errors = new ArrayList<ValidationError>();
            if (password == null) {
                errors.add(ValidationError.nullValue());
                return errors;
            }
            if (password.length() < MIN_LENGTH) {
                errors.add(ValidationError.belowMinLength(MIN_LENGTH));
            }
            if (password.length() > MAX_LENGTH) {
                errors.add(ValidationError.aboveMaxLength(MAX_LENGTH));
            }
            if (!containsAny(password, LOWER_CASE.toCharArray())) {
                errors.add(new ValidationError(Error.NO_LOWER_CASE));
            }
            if (!containsAny(password, UPPER_CASE.toCharArray())) {
                errors.add(new ValidationError(Error.NO_UPPER_CASE));
            }
            if (!containsAny(password, DIGIT.toCharArray())) {
                errors.add(new ValidationError(Error.NO_DIGIT));
            }
            if (!containsAny(password, SPECIAL.toCharArray())) {
                errors.add(new ValidationError(Error.NO_SPECIAL));
            }
            if (!containsOnly(password, ALL.toCharArray())) {
                errors.add(new ValidationError(Error.INVALID_CHARACTER));
            }
            return errors;
        }

        private static boolean containsAny(String password, char[] chars) {
            char[] passwordCharArray = password.toCharArray();
            for (char character : chars) {
                if (contains(passwordCharArray, character)) {
                    return true;
                }
            }
            return false;
        }

        private static boolean containsOnly(String password, char[] chars) {
            for (char character : password.toCharArray()) {
                if (!contains(chars, character)) {
                    return false;
                }
            }
            return true;
        }

        private static boolean contains(char[] charArray, char character) {
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

    public enum Error {
        NO_LOWER_CASE,
        NO_UPPER_CASE,
        NO_DIGIT,
        NO_SPECIAL,
        INVALID_CHARACTER
    }
}
