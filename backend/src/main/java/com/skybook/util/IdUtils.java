package com.skybook.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.UUID;

public final class IdUtils {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final char[] REF_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();

    private IdUtils() {
    }

    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    public static String bookingReference() {
        StringBuilder sb = new StringBuilder("SBK");
        for (int i = 0; i < 5; i++) {
            sb.append(REF_CHARS[RANDOM.nextInt(REF_CHARS.length)]);
        }
        return sb.toString();
    }

    public static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String opaqueToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}
