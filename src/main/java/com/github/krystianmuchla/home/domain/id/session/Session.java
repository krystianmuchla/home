package com.github.krystianmuchla.home.domain.id.session;

import com.github.krystianmuchla.home.domain.id.user.User;

public class Session {
    public final User user;

    public Session(User user) {
        if (user == null) {
            throw new IllegalArgumentException();
        }
        this.user = user;
    }
}
