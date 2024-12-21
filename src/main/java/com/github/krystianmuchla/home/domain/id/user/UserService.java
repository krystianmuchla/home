package com.github.krystianmuchla.home.domain.id.user;

import com.github.krystianmuchla.home.application.util.StringService;
import com.github.krystianmuchla.home.domain.id.accessdata.AccessDataService;
import com.github.krystianmuchla.home.domain.id.accessdata.error.AccessDataAlreadyExistsException;
import com.github.krystianmuchla.home.domain.id.accessdata.error.AccessDataValidationException;
import com.github.krystianmuchla.home.domain.id.error.UnauthenticatedException;
import com.github.krystianmuchla.home.domain.id.password.Secret;
import com.github.krystianmuchla.home.domain.id.password.error.PasswordValidationException;
import com.github.krystianmuchla.home.domain.id.user.error.UserBlockedException;
import com.github.krystianmuchla.home.domain.id.user.error.UserNotFoundException;
import com.github.krystianmuchla.home.domain.id.user.error.UserValidationException;
import com.github.krystianmuchla.home.infrastructure.persistence.id.AccessDataPersistence;
import com.github.krystianmuchla.home.infrastructure.persistence.id.UserPersistence;

import java.util.Arrays;
import java.util.UUID;

public class UserService {
    public static UUID create(String name, String login, String password) throws UserValidationException, PasswordValidationException, AccessDataAlreadyExistsException, AccessDataValidationException {
        var user = new User(name);
        UserPersistence.create(user);
        AccessDataService.create(user.id, login, password);
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
