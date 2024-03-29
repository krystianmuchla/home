package com.github.krystianmuchla.home.id.user;

import com.github.krystianmuchla.home.error.exception.AuthenticationException;
import com.github.krystianmuchla.home.error.exception.ConflictException;
import com.github.krystianmuchla.home.error.exception.InternalException;
import com.github.krystianmuchla.home.id.SecureRandomFactory;
import com.github.krystianmuchla.home.id.accessdata.AccessData;
import com.github.krystianmuchla.home.id.accessdata.AccessDataSql;
import lombok.SneakyThrows;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

public class UserService {
    private static final SecretKeyFactory SECRET_KEY_FACTORY;
    private static final int SALT_BYTES = 32;
    private static final int SECRET_BYTES = 32;

    static {
        try {
            SECRET_KEY_FACTORY = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        } catch (final NoSuchAlgorithmException exception) {
            throw new InternalException(exception);
        }
    }

    public static User createUser(final String login, final String password) {
        var accessData = AccessDataSql.readByLogin(login);
        if (accessData != null) {
            throw new ConflictException("USER_ALREADY_EXISTS");
        }
        final var user = new User(UUID.randomUUID());
        UserSql.create(user);
        final var salt = SecureRandomFactory.createBytes(SALT_BYTES);
        final var secret = secret(salt, password);
        accessData = new AccessData(UUID.randomUUID(), user.id(), login, salt, secret);
        AccessDataSql.create(accessData);
        return user;
    }

    public static User getUser(final String login, final String password) {
        final var accessData = AccessDataSql.readByLogin(login);
        if (accessData == null) {
            throw new AuthenticationException();
        }
        UserAuthService.validateAuth(accessData.userId());
        final var secret = secret(accessData.salt(), password);
        if (!Arrays.equals(secret, accessData.secret())) {
            throw new AuthenticationException(accessData.userId());
        }
        return UserSql.readById(accessData.userId());
    }

    @SneakyThrows
    private static byte[] secret(final byte[] salt, final String password) {
        final var keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536, SECRET_BYTES * 8);
        return SECRET_KEY_FACTORY.generateSecret(keySpec).getEncoded();
    }
}
