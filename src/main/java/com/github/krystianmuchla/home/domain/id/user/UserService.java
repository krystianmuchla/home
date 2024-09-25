package com.github.krystianmuchla.home.domain.id.user;

import com.github.krystianmuchla.home.application.exception.InternalException;
import com.github.krystianmuchla.home.domain.id.SecureRandomFactory;
import com.github.krystianmuchla.home.domain.id.accessdata.AccessData;
import com.github.krystianmuchla.home.domain.id.accessdata.AccessDataPersistence;
import com.github.krystianmuchla.home.infrastructure.http.exception.ConflictException;
import com.github.krystianmuchla.home.infrastructure.http.exception.NotFoundException;
import com.github.krystianmuchla.home.infrastructure.http.exception.UnauthorizedException;

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

    public static UUID create(String name, String login, String password) {
        var accessData = AccessDataPersistence.read(login);
        if (accessData != null) {
            throw new ConflictException("USER_ALREADY_EXISTS");
        }
        var user = new User(name);
        UserPersistence.create(user);
        var salt = SecureRandomFactory.createBytes(SALT_BYTES);
        var secret = secret(salt, password);
        accessData = new AccessData(user.id, login, salt, secret);
        AccessDataPersistence.create(accessData);
        return user.id;
    }

    public static User get(String login, String password) {
        var accessData = AccessDataPersistence.read(login);
        if (accessData == null) {
            throw new UnauthorizedException();
        }
        UserGuardService.inspect(accessData.userId);
        var secret = secret(accessData.salt, password);
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
