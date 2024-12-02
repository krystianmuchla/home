package com.github.krystianmuchla.home.domain.id.user;

import com.github.krystianmuchla.home.application.util.StringService;
import com.github.krystianmuchla.home.domain.id.AccessData;
import com.github.krystianmuchla.home.domain.id.exception.UnauthenticatedException;
import com.github.krystianmuchla.home.domain.id.user.exception.UserAlreadyExistsException;
import com.github.krystianmuchla.home.domain.id.user.exception.UserBlockedException;
import com.github.krystianmuchla.home.domain.id.user.exception.UserNotFoundException;
import com.github.krystianmuchla.home.infrastructure.persistence.id.AccessDataPersistence;
import com.github.krystianmuchla.home.infrastructure.persistence.id.UserPersistence;

import java.util.Arrays;
import java.util.UUID;

public class UserService {
    public static UUID create(String name, String login, String password) throws UserAlreadyExistsException {
        var accessData = AccessDataPersistence.read(login);
        if (accessData != null) {
            throw new UserAlreadyExistsException();
        }
        var user = new User(name);
        UserPersistence.create(user);
        var secret = new Secret(password);
        accessData = new AccessData(user.id, login, secret);
        AccessDataPersistence.create(accessData);
        return user.id;
    }

    public static User get(String login, String password) throws UnauthenticatedException, UserBlockedException {
        if (StringService.isEmpty(password)) {
            throw new UnauthenticatedException();
        }
        var accessData = AccessDataPersistence.read(login);
        if (accessData == null) {
            throw new UnauthenticatedException();
        }
        UserGuardService.inspect(accessData.userId);
        var secret = Secret.from(accessData.salt, password);
        if (!Arrays.equals(secret, accessData.secret)) {
            UserGuardService.incrementAuthFailures(accessData.userId);
            throw new UnauthenticatedException();
        }
        try {
            return get(accessData.userId);
        } catch (UserNotFoundException exception) {
            throw new IllegalStateException(exception);
        }
    }

    public static User get(UUID id) throws UserNotFoundException {
        var user = UserPersistence.read(id);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }
}
