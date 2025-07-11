package com.pulseband.pulseband.decypher;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.zip.CRC32;

public class Decypher {

    private static final byte[] keyBytes = new byte[]{
            (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67,
            (byte) 0x89, (byte) 0xab, (byte) 0xcd, (byte) 0xef,
            (byte) 0xfe, (byte) 0xdc, (byte) 0xba, (byte) 0x98,
            (byte) 0x76, (byte) 0x54, (byte) 0x32, (byte) 0x10,
            (byte) 0x89, (byte) 0xab, (byte) 0xcd, (byte) 0xef,
            (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67
    };

    public static String decryptMessage(String base64Input) throws Exception {
        byte[] fullMessage = Base64.getDecoder().decode(base64Input);

        if (fullMessage.length <= 4) {
            throw new IllegalArgumentException("Message too short.");
        }

        int dataLength = fullMessage.length - 4;
        byte[] encrypted = new byte[dataLength];
        byte[] crcBytes = new byte[4];

        System.arraycopy(fullMessage, 0, encrypted, 0, dataLength);
        System.arraycopy(fullMessage, dataLength, crcBytes, 0, 4);

        CRC32 crc32 = new CRC32();
        crc32.update(encrypted);
        long calculatedCrc = crc32.getValue();

        long receivedCrc = ((crcBytes[3] & 0xFFL) << 24)
                | ((crcBytes[2] & 0xFFL) << 16)
                | ((crcBytes[1] & 0xFFL) << 8)
                | (crcBytes[0] & 0xFFL);

        if (calculatedCrc != receivedCrc) {
            throw new SecurityException("Invalid CRC!");
        }

        return decrypt3DES(encrypted).trim();
    }

    private static String decrypt3DES(byte[] encrypted) throws Exception {
        SecretKey key = new SecretKeySpec(keyBytes, "DESede");
        Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(encrypted);
        return new String(decryptedBytes);
    }
}
