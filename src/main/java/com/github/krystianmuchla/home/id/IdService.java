package com.github.krystianmuchla.home.id;

import java.util.Arrays;
import java.util.UUID;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.github.krystianmuchla.home.error.exception.AuthorizationException;
import com.github.krystianmuchla.home.error.exception.ConflictException;
import com.github.krystianmuchla.home.id.accessdata.AccessData;
import com.github.krystianmuchla.home.id.accessdata.AccessDataDao;
import com.github.krystianmuchla.home.id.user.User;
import com.github.krystianmuchla.home.id.user.UserDao;

import lombok.SneakyThrows;

public class IdService {
    public static final IdService INSTANCE = new IdService();

    private static final int SALT_BYTES = 32;
    private static final int SECRET_BYTES = 32;

    private final UserDao userDao;
    private final AccessDataDao accessDataDao;
    private final SecretKeyFactory secretFactory;

    @SneakyThrows
    private IdService() {
        userDao = UserDao.INSTANCE;
        accessDataDao = AccessDataDao.INSTANCE;
        secretFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    }

    public User createUser(final String login, final String password) {
        var accessData = accessDataDao.readByLogin(login);
        if (accessData != null) {
            throw new ConflictException("USER_ALREADY_EXISTS");
        }
        final var user = new User(UUID.randomUUID());
        userDao.create(user);
        final var salt = SecureRandomFactory.createBytes(SALT_BYTES);
        final var secret = secret(salt, password);
        accessData = new AccessData(UUID.randomUUID(), user.id(), login, salt, secret);
        accessDataDao.create(accessData);
        return user;
    }

    public User getUser(final String login, final String password) {
        final var accessData = accessDataDao.readByLogin(login);
        if (accessData == null) {
            throw new AuthorizationException();
        }
        final var secret = secret(accessData.salt(), password);
        if (!Arrays.equals(secret, accessData.secret())) {
            throw new AuthorizationException();
        }
        return userDao.readById(accessData.user_id());
    }

    @SneakyThrows
    private byte[] secret(final byte[] salt, final String password) {
        final var keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536, SECRET_BYTES * 8);
        return secretFactory.generateSecret(keySpec).getEncoded();
    }
}
