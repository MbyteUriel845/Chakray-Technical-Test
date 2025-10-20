package com.chakray.technical_test.security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class AesUtil {
    private final byte[] keyBytes;

    public AesUtil(String hexKey) {
        this.keyBytes = hexStringToByteArray(hexKey);
    }

    public String encrypt(String plain) {
        try {
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(plain.getBytes("UTF-8"));

            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public String decrypt(String base64IvCipher) {
        try {
            byte[] combined = Base64.getDecoder().decode(base64IvCipher);
            byte[] iv = new byte[16];
            System.arraycopy(combined, 0, iv, 0, 16);
            int ctLen = combined.length - 16;
            byte[] ct = new byte[ctLen];
            System.arraycopy(combined, 16, ct, 0, ctLen);

            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] plain = cipher.doFinal(ct);
            return new String(plain, "UTF-8");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len/2];
        for (int i=0;i<len;i+=2) {
            data[i/2] = (byte) ((Character.digit(s.charAt(i),16) << 4) + Character.digit(s.charAt(i+1),16));
        }
        return data;
    }
}
