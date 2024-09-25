package com.github.krystianmuchla.home.domain.id;

import java.security.SecureRandom;

public class SecureRandomFactory {
    private static final SecureRandom secureRandom = new SecureRandom();

    public static byte[] createBytes(int bytes) {
        byte[] result = new byte[bytes];
        secureRandom.nextBytes(result);
        return result;
    }

    public static int[] createIntegers(int integers, int bound) {
        int[] result = new int[integers];
        for (int index = 0; index < integers; index++) {
            result[index] = secureRandom.nextInt(bound);
        }
        return result;
    }
}
