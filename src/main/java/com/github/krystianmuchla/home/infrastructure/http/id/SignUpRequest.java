package com.github.krystianmuchla.home.infrastructure.http.id;

import com.github.krystianmuchla.home.infrastructure.http.core.RequestBody;

public record SignUpRequest(String name, String login, String password, String token) implements RequestBody {
}
