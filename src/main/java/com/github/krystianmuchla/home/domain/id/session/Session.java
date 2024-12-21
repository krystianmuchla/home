package com.github.krystianmuchla.home.domain.id.session;

import com.github.krystianmuchla.home.domain.id.session.error.SessionValidationException;
import com.github.krystianmuchla.home.domain.id.user.User;

public class Session {
    public final User user;

    public Session(User user) throws SessionValidationException {
        this.user = user;
        SessionValidator.validate(this);
    }
}
