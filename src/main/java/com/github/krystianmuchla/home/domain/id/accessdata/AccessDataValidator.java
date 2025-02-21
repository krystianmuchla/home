package com.github.krystianmuchla.home.domain.id.accessdata;

import com.github.krystianmuchla.home.domain.core.error.Validator;
import com.github.krystianmuchla.home.domain.id.accessdata.error.AccessDataValidationError;
import com.github.krystianmuchla.home.domain.id.accessdata.error.AccessDataValidationException;

public class AccessDataValidator extends Validator<AccessDataValidationError, AccessDataValidationException> {
    private static final int LOGIN_MAX_LENGTH = 50;
    private static final int SALT_LENGTH = 32;
    private static final int SECRET_LENGTH = 32;
    private static final int VERSION_MIN_VALUE = 1;

    private final AccessData accessData;

    public AccessDataValidator(AccessData accessData) {
        this.accessData = accessData;
    }

    public AccessDataValidator checkId() {
        if (accessData.id == null) {
            errors.add(new AccessDataValidationError.NullId());
        }
        return this;
    }

    public AccessDataValidator checkUserId() {
        if (accessData.userId == null) {
            errors.add(new AccessDataValidationError.NullUserId());
        }
        return this;
    }

    public AccessDataValidator checkLogin() {
        if (accessData.login == null) {
            errors.add(new AccessDataValidationError.NullLogin());
        } else {
            if (accessData.login.isEmpty()) {
                errors.add(new AccessDataValidationError.LoginBelowMinLength(1));
            }
            if (accessData.login.length() > LOGIN_MAX_LENGTH) {
                errors.add(new AccessDataValidationError.LoginAboveMaxLength(LOGIN_MAX_LENGTH));
            }
        }
        return this;
    }

    public AccessDataValidator checkSalt() {
        if (accessData.salt == null) {
            errors.add(new AccessDataValidationError.NullSalt());
        } else {
            if (accessData.salt.length < SALT_LENGTH) {
                errors.add(new AccessDataValidationError.SaltBelowMinLength(SALT_LENGTH));
            }
            if (accessData.salt.length > SALT_LENGTH) {
                errors.add(new AccessDataValidationError.SaltAboveMaxLength(SALT_LENGTH));
            }
        }
        return this;
    }

    public AccessDataValidator checkSecret() {
        if (accessData.secret == null) {
            errors.add(new AccessDataValidationError.NullSecret());
        } else {
            if (accessData.secret.length < SECRET_LENGTH) {
                errors.add(new AccessDataValidationError.SecretBelowMinLength(SECRET_LENGTH));
            }
            if (accessData.secret.length > SECRET_LENGTH) {
                errors.add(new AccessDataValidationError.SecretAboveMaxLength(SECRET_LENGTH));
            }
        }
        return this;
    }

    public AccessDataValidator checkCreationTime() {
        if (accessData.creationTime == null) {
            errors.add(new AccessDataValidationError.NullCreationTime());
        }
        return this;
    }

    public AccessDataValidator checkModificationTime() {
        if (accessData.modificationTime == null) {
            errors.add(new AccessDataValidationError.NullModificationTime());
        }
        return this;
    }

    public AccessDataValidator checkVersion() {
        if (accessData.version == null) {
            errors.add(new AccessDataValidationError.NullVersion());
        } else if (accessData.version < VERSION_MIN_VALUE) {
            errors.add(new AccessDataValidationError.VersionBelowMinValue(VERSION_MIN_VALUE));
        }
        return this;
    }

    @Override
    public void validate() throws AccessDataValidationException {
        if (hasErrors()) {
            throw new AccessDataValidationException(errors);
        }
    }

    public static void validate(AccessData accessData) throws AccessDataValidationException {
        new AccessDataValidator(accessData)
            .checkId()
            .checkUserId()
            .checkLogin()
            .checkSalt()
            .checkSecret()
            .checkCreationTime()
            .checkModificationTime()
            .checkVersion()
            .validate();
    }
}
