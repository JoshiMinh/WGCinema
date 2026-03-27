package com.joshiminh.wgcinema.util;

import java.util.prefs.Preferences;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * SessionManager handles secure storage of user credentials using Java
 * Preferences API
 * with AES encryption for the password.
 */
public class SessionManager {
    private static final String PREF_NODE = "com.joshiminh.wgcinema";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_PASSWORD = "user_password";

    // Simple key derivation from system properties to make it machine-specific
    private static final String SECRET_SEED = System.getProperty("user.name") + "WGCinemaSecretSeed";

    private static Preferences getPrefs() {
        return Preferences.userRoot().node(PREF_NODE);
    }

    public static void saveSession(String email, String password) {
        Preferences prefs = getPrefs();
        prefs.put(KEY_EMAIL, email);
        if (password != null && !password.isEmpty()) {
            String encrypted = encrypt(password);
            if (encrypted != null) {
                prefs.put(KEY_PASSWORD, encrypted);
            }
        } else {
            prefs.remove(KEY_PASSWORD);
        }
    }

    public static String getEmail() {
        return getPrefs().get(KEY_EMAIL, null);
    }

    public static String getPassword() {
        String encrypted = getPrefs().get(KEY_PASSWORD, null);
        if (encrypted != null) {
            return decrypt(encrypted);
        }
        return null;
    }

    public static void clearSession() {
        Preferences prefs = getPrefs();
        prefs.remove(KEY_EMAIL);
        prefs.remove(KEY_PASSWORD);
    }

    // --- AES Encryption Utilities ---

    private static String encrypt(String value) {
        try {
            SecretKeySpec secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            System.err.println("Encryption error: " + e.getMessage());
            return null;
        }
    }

    private static String decrypt(String encryptedValue) {
        try {
            SecretKeySpec secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedValue = Base64.getDecoder().decode(encryptedValue);
            byte[] decrypted = cipher.doFinal(decodedValue);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("Decryption error: " + e.getMessage());
            return null;
        }
    }

    private static SecretKeySpec getSecretKey() throws Exception {
        byte[] key = SECRET_SEED.getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        key = sha.digest(key);
        byte[] key16 = new byte[16];
        System.arraycopy(key, 0, key16, 0, 16);
        return new SecretKeySpec(key16, "AES");
    }
}