package com.github.krystianmuchla.home.id.user;

import com.github.krystianmuchla.home.exception.InternalException;
import com.github.krystianmuchla.home.exception.http.ConflictException;
import com.github.krystianmuchla.home.exception.http.UnauthorizedException;
import com.github.krystianmuchla.home.id.SecureRandomFactory;
import com.github.krystianmuchla.home.id.accessdata.AccessData;
import com.github.krystianmuchla.home.id.accessdata.AccessDataPersistence;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.UUID;

public class UserService {
    private static final SecretKeyFactory SECRET_KEY_FACTORY;
    private static final int SALT_BYTES = 32;
    private static final int SECRET_BYTES = 32;

    static {
        try {
            SECRET_KEY_FACTORY = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        } catch (NoSuchAlgorithmException exception) {
            throw new InternalException(exception);
        }
    }

    public static User createUser(String name, String login, String password) {
        var accessData = AccessDataPersistence.read(login);
        if (accessData != null) {
            throw new ConflictException("USER_ALREADY_EXISTS");
        }
        var user = new User(UUID.randomUUID(), name);
        UserPersistence.create(user);
        var salt = SecureRandomFactory.createBytes(SALT_BYTES);
        var secret = secret(salt, password);
        accessData = new AccessData(UUID.randomUUID(), user.id(), login, salt, secret);
        AccessDataPersistence.create(accessData);
        return user;
    }

    public static User getUser(String login, String password) {
        var accessData = AccessDataPersistence.read(login);
        if (accessData == null) {
            throw new UnauthorizedException();
        }
        UserGuardService.inspect(accessData.userId());
        var secret = secret(accessData.salt(), password);
        if (!Arrays.equals(secret, accessData.secret())) {
            throw new UnauthorizedException(accessData.userId());
        }
        return UserPersistence.read(accessData.userId());
    }

    private static byte[] secret(byte[] salt, String password) {
        var keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536, SECRET_BYTES * 8);
        SecretKey secret;
        try {
            secret = SECRET_KEY_FACTORY.generateSecret(keySpec);
        } catch (InvalidKeySpecException exception) {
            throw new InternalException(exception);
        }
        return secret.getEncoded();
    }
}
