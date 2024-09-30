package com.github.krystianmuchla.home.domain.id.session;

import com.github.krystianmuchla.home.domain.id.user.User;

import static com.github.krystianmuchla.home.domain.id.IdValidator.validateUser;

public record Session(User user) {
    public Session {
        assert validateUser(user).isEmpty();
    }
}
