package com.github.krystianmuchla.home.domain.id.user;

import com.github.krystianmuchla.home.application.util.StringService;
import com.github.krystianmuchla.home.domain.id.accessdata.AccessData;
import com.github.krystianmuchla.home.domain.id.accessdata.AccessDataPersistence;
import com.github.krystianmuchla.home.infrastructure.http.exception.ConflictException;
import com.github.krystianmuchla.home.infrastructure.http.exception.NotFoundException;
import com.github.krystianmuchla.home.infrastructure.http.exception.UnauthorizedException;

import java.util.Arrays;
import java.util.UUID;

public class UserService {
    public static UUID create(String name, String login, String password) {
        var accessData = AccessDataPersistence.read(login);
        if (accessData != null) {
            throw new ConflictException("USER_ALREADY_EXISTS");
        }
        var user = new User(name);
        UserPersistence.create(user);
        var secret = new Secret(password);
        accessData = new AccessData(user.id, login, secret);
        AccessDataPersistence.create(accessData);
        return user.id;
    }

    public static User get(String login, String password) {
        if (StringService.isEmpty(password)) {
            throw new UnauthorizedException();
        }
        var accessData = AccessDataPersistence.read(login);
        if (accessData == null) {
            throw new UnauthorizedException();
        }
        UserGuardService.inspect(accessData.userId);
        var secret = Secret.from(accessData.salt, password);
        if (!Arrays.equals(secret, accessData.secret)) {
            throw new UnauthorizedException(accessData.userId);
        }
        return get(accessData.userId);
    }

    public static User get(UUID id) {
        var user = UserPersistence.read(id);
        if (user == null) {
            throw new NotFoundException();
        }
        return user;
    }
}
