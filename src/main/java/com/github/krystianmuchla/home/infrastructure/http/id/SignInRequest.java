package com.github.krystianmuchla.home.infrastructure.http.id;

import com.github.krystianmuchla.home.infrastructure.http.core.RequestBody;

public record SignInRequest(String login, String password) implements RequestBody {
}
