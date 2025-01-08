package com.github.krystianmuchla.home.domain.id.user;

import com.github.krystianmuchla.home.application.util.StringService;
import com.github.krystianmuchla.home.domain.id.SecureRandomFactory;
import com.github.krystianmuchla.home.domain.id.accessdata.AccessDataService;
import com.github.krystianmuchla.home.domain.id.accessdata.error.AccessDataAlreadyExistsException;
import com.github.krystianmuchla.home.domain.id.accessdata.error.AccessDataValidationException;
import com.github.krystianmuchla.home.domain.id.error.UnauthenticatedException;
import com.github.krystianmuchla.home.domain.id.password.Secret;
import com.github.krystianmuchla.home.domain.id.password.error.PasswordValidationException;
import com.github.krystianmuchla.home.domain.id.user.error.UserNotFoundException;
import com.github.krystianmuchla.home.domain.id.user.error.UserValidationException;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Transaction;
import com.github.krystianmuchla.home.infrastructure.persistence.id.accessdata.AccessDataPersistence;
import com.github.krystianmuchla.home.infrastructure.persistence.id.user.UserPersistence;

import java.util.Arrays;
import java.util.UUID;

public class UserService {
    public static final UserService INSTANCE = new UserService(AccessDataService.INSTANCE);

    private final AccessDataService accessDataService;

    public UserService(AccessDataService accessDataService) {
        this.accessDataService = accessDataService;
    }

    public UUID create(String name, String login, String password) throws UserValidationException, PasswordValidationException, AccessDataAlreadyExistsException, AccessDataValidationException {
        var user = new User(name);
        Transaction.run(() -> {
            UserPersistence.create(user);
            accessDataService.create(user.id, login, password);
        });
        return user.id;
    }

    public User get(String login, String password) throws UnauthenticatedException {
        var processingTime = SecureRandomFactory.createInteger(1000, 2000);
        var processingStartTime = System.currentTimeMillis();
        User user = null;
        if (!StringService.isEmpty(password)) {
            var accessData = AccessDataPersistence.read(login);
            if (accessData != null) {
                var secret = new Secret(accessData.salt, password);
                if (Arrays.equals(secret.secret, accessData.secret)) {
                    try {
                        user = get(accessData.userId);
                    } catch (UserNotFoundException exception) {
                        throw new IllegalStateException(exception);
                    }
                }
            }
        }
        var processingEndTime = System.currentTimeMillis();
        var timeLeft = processingTime - (processingEndTime - processingStartTime);
        if (timeLeft > 0) {
            try {
                Thread.sleep(timeLeft);
            } catch (InterruptedException exception) {
                throw new RuntimeException(exception);
            }
        }
        if (user == null) {
            throw new UnauthenticatedException();
        }
        return user;
    }

    public User get(UUID id) throws UserNotFoundException {
        var user = UserPersistence.read(id);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }
}
