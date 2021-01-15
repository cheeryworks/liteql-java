package org.cheeryworks.liteql.skeleton.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public abstract class StringEncoder {

    private static MessageDigest md5MessageDigest;

    private static final char[] HEX = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};

    static {
        try {
            md5MessageDigest = MessageDigest.getInstance("Md5");
        } catch (Exception ex) {
        }
    }

    public static String md5(String constraintName) {
        byte[] digest = md5MessageDigest.digest(constraintName.getBytes(StandardCharsets.UTF_8));

        return new String(encode(digest));
    }

    private static char[] encode(byte[] bytes) {
        final int nBytes = bytes.length;
        char[] result = new char[2 * nBytes];

        int j = 0;
        for (int i = 0; i < nBytes; i++) {
            // Char for top 4 bits
            result[j++] = HEX[(0xF0 & bytes[i]) >>> 4];
            // Bottom 4
            result[j++] = HEX[(0x0F & bytes[i])];
        }

        return result;
    }

}
