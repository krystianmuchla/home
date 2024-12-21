package com.github.krystianmuchla.home.domain.id.session.error;

public abstract sealed class SessionValidationError permits SessionValidationError.NullUser {
    public static final class NullUser extends SessionValidationError {
    }
}
