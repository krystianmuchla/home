package com.github.krystianmuchla.home.id;

import java.security.SecureRandom;

public class SecureRandomFactory {
    private static final SecureRandom secureRandom = new SecureRandom();

    public static byte[] create(final int bytes) {
        final byte[] result = new byte[bytes];
        secureRandom.nextBytes(result);
        return result;
    }
}
