package com.github.krystianmuchla.home.domain.id.password;

import com.github.krystianmuchla.home.domain.id.SecureRandomFactory;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Secret {
    private static final SecretKeyFactory SECRET_KEY_FACTORY;
    private static final int SALT_BYTES = 32;
    private static final int SECRET_BYTES = 32;

    static {
        try {
            SECRET_KEY_FACTORY = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    public final byte[] salt;
    public final byte[] secret;

    public Secret(Password password) {
        if (password == null) {
            throw new IllegalArgumentException();
        }
        salt = SecureRandomFactory.createBytes(SALT_BYTES);
        secret = Secret.from(salt, password.value);
    }

    // todo
    public static byte[] from(byte[] salt, String password) {
        var keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536, SECRET_BYTES * 8);
        SecretKey secret;
        try {
            secret = SECRET_KEY_FACTORY.generateSecret(keySpec);
        } catch (InvalidKeySpecException exception) {
            throw new IllegalStateException(exception);
        }
        return secret.getEncoded();
    }
}
