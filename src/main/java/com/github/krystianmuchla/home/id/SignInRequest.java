package com.github.krystianmuchla.home.id;

import com.github.krystianmuchla.home.api.RequestBody;

public record SignInRequest(String login, String password) implements RequestBody {
    @Override
    public void validate() {
        if (login == null) {
            throw new IllegalArgumentException();
        }
        if (password == null) {
            throw new IllegalArgumentException();
        }
    }
}
