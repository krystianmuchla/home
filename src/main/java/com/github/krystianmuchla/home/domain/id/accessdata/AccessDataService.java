package com.github.krystianmuchla.home.domain.id.accessdata;

import com.github.krystianmuchla.home.domain.id.accessdata.error.AccessDataAlreadyExistsException;
import com.github.krystianmuchla.home.domain.id.accessdata.error.AccessDataValidationException;
import com.github.krystianmuchla.home.domain.id.password.Password;
import com.github.krystianmuchla.home.domain.id.password.Secret;
import com.github.krystianmuchla.home.domain.id.password.error.PasswordValidationException;
import com.github.krystianmuchla.home.infrastructure.persistence.id.accessdata.AccessDataPersistence;

import java.util.UUID;

public class AccessDataService {
    public static final AccessDataService INSTANCE = new AccessDataService();

    public void create(UUID userId, String login, String password) throws AccessDataAlreadyExistsException, PasswordValidationException, AccessDataValidationException {
        var accessData = AccessDataPersistence.read(login);
        if (accessData != null) {
            throw new AccessDataAlreadyExistsException();
        }
        var secret = createSecret(password);
        accessData = new AccessData(userId, login, secret.salt, secret.secret);
        AccessDataPersistence.create(accessData);
    }

    private static Secret createSecret(String password) throws PasswordValidationException {
        return new Secret(new Password(password));
    }
}
