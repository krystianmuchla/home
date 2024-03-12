package com.github.krystianmuchla.home.id;

import java.security.SecureRandom;

public class SecureRandomFactory {
    private static final SecureRandom secureRandom = new SecureRandom();

    public static byte[] createBytes(final int bytes) {
        final byte[] result = new byte[bytes];
        secureRandom.nextBytes(result);
        return result;
    }

    public static int[] createIntegers(final int integers, final int bound) {
        final int[] result = new int[integers];
        for (int index = 0; index < integers; index++) {
            result[index] = secureRandom.nextInt(bound);
        }
        return result;
    }
}
