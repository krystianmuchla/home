package com.github.krystianmuchla.home.domain.id.user;

import com.github.krystianmuchla.home.domain.core.error.Validator;
import com.github.krystianmuchla.home.domain.id.user.error.UserValidationError;
import com.github.krystianmuchla.home.domain.id.user.error.UserValidationException;

public class UserValidator extends Validator<UserValidationError, UserValidationException> {
    private static final int NAME_MAX_LENGTH = 100;
    private static final int VERSION_MIN_VALUE = 1;

    private final User user;

    public UserValidator(User user) {
        this.user = user;
    }

    public UserValidator checkId() {
        if (user.id == null) {
            errors.add(new UserValidationError.NullId());
        }
        return this;
    }

    public UserValidator checkName() {
        if (user.name == null) {
            errors.add(new UserValidationError.NullName());
        } else {
            if (user.name.isBlank()) {
                errors.add(new UserValidationError.NameBelowMinLength(1));
            }
            if (user.name.length() > NAME_MAX_LENGTH) {
                errors.add(new UserValidationError.NameAboveMaxLength(NAME_MAX_LENGTH));
            }
        }
        return this;
    }

    public UserValidator checkCreationTime() {
        if (user.creationTime == null) {
            errors.add(new UserValidationError.NullCreationTime());
        }
        return this;
    }

    public UserValidator checkModificationTime() {
        if (user.modificationTime == null) {
            errors.add(new UserValidationError.NullModificationTime());
        }
        return this;
    }

    public UserValidator checkVersion() {
        if (user.version == null) {
            errors.add(new UserValidationError.NullVersion());
        } else if (user.version < VERSION_MIN_VALUE) {
            errors.add(new UserValidationError.VersionBelowMinValue(VERSION_MIN_VALUE));
        }
        return this;
    }

    @Override
    public void validate() throws UserValidationException {
        if (hasErrors()) {
            throw new UserValidationException(errors);
        }
    }

    public static void validate(User user) throws UserValidationException {
        new UserValidator(user)
            .checkId()
            .checkName()
            .checkCreationTime()
            .checkModificationTime()
            .checkVersion()
            .validate();
    }
}
