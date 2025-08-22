package com.tonic.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;

/**
 * @author Gaurav Purwar
 * Utility class for AES encryption and decryption operations
 * Provides functionality to store encrypted values with keys and retrieve them decrypted at runtime
 * Uses AES-GCM encryption for strong security
 */
public class EncryptionUtils {
    
    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12; // 96 bits
    private static final int GCM_TAG_LENGTH = 16; // 128 bits
    
    private static final Map<String, String> encryptedDataStore = new HashMap<>();
    private static final String DEFAULT_SECRET_KEY = "";
    
    private EncryptionUtils() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Generates a SecretKey from the provided key string
     * @param secretKey the secret key string
     * @return SecretKey object for AES encryption
     */
    private static SecretKey getSecretKey(String secretKey) {
        // Ensure key is exactly 16 bytes for AES-128
        byte[] keyBytes = new byte[16];
        byte[] inputBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(inputBytes, 0, keyBytes, 0, Math.min(inputBytes.length, keyBytes.length));
        return new SecretKeySpec(keyBytes, AES_ALGORITHM);
    }
    
    /**
     * Encrypts a string using AES-GCM encryption with the default secret key
     * @param plainText the string to encrypt
     * @return Base64 encoded encrypted string (includes IV + encrypted data)
     */
    public static String encrypt(String plainText) {
        return encrypt(plainText, DEFAULT_SECRET_KEY);
    }
    
    /**
     * Encrypts a string using AES-GCM encryption with a custom secret key
     * @param plainText the string to encrypt
     * @param secretKey the secret key to use for encryption
     * @return Base64 encoded encrypted string (includes IV + encrypted data)
     */
    public static String encrypt(String plainText, String secretKey) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        
        try {
            SecretKey aesKey = getSecretKey(secretKey);
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            
            // Generate random IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, parameterSpec);
            byte[] encryptedData = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            
            // Combine IV + encrypted data
            byte[] encryptedWithIv = new byte[GCM_IV_LENGTH + encryptedData.length];
            System.arraycopy(iv, 0, encryptedWithIv, 0, GCM_IV_LENGTH);
            System.arraycopy(encryptedData, 0, encryptedWithIv, GCM_IV_LENGTH, encryptedData.length);
            
            return Base64.getEncoder().encodeToString(encryptedWithIv);
            
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }
    
    /**
     * Decrypts an AES-GCM encrypted string using the default secret key
     * @param encryptedText the Base64 encoded encrypted string to decrypt
     * @return decrypted plain text string
     */
    public static String decrypt(String encryptedText) {
        return decrypt(encryptedText, DEFAULT_SECRET_KEY);
    }
    
    /**
     * Decrypts an AES-GCM encrypted string using a custom secret key
     * @param encryptedText the Base64 encoded encrypted string to decrypt
     * @param secretKey the secret key to use for decryption
     * @return decrypted plain text string
     */
    public static String decrypt(String encryptedText, String secretKey) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }
        
        try {
            SecretKey aesKey = getSecretKey(secretKey);
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            
            byte[] encryptedWithIv = Base64.getDecoder().decode(encryptedText);
            
            // Extract IV and encrypted data
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encryptedData = new byte[encryptedWithIv.length - GCM_IV_LENGTH];
            System.arraycopy(encryptedWithIv, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(encryptedWithIv, GCM_IV_LENGTH, encryptedData, 0, encryptedData.length);
            
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, parameterSpec);
            
            byte[] decryptedData = cipher.doFinal(encryptedData);
            return new String(decryptedData, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
    
    /**
     * Debug utility for decryption: prints encrypted value, key, and raw bytes before decrypting.
     */
    public static String debugDecrypt(String encryptedText, String secretKey, String context) {
        System.out.println("[DEBUG-DECRYPT] Context: " + context);
        System.out.println("[DEBUG-DECRYPT] Encrypted value: " + encryptedText);
        System.out.println("[DEBUG-DECRYPT] Secret key: " + secretKey);
        if (encryptedText != null) {
            byte[] bytes = null;
            try {
                bytes = java.util.Base64.getDecoder().decode(encryptedText);
                System.out.println("[DEBUG-DECRYPT] Raw bytes (Base64 decoded, length=" + bytes.length + "): " + java.util.Arrays.toString(java.util.Arrays.copyOf(bytes, Math.min(bytes.length, 32))) + (bytes.length > 32 ? "..." : ""));
            } catch (Exception e) {
                System.out.println("[DEBUG-DECRYPT] Error decoding Base64: " + e.getMessage());
            }
        }
        return decrypt(encryptedText, secretKey);
    }
    
    /**
     * Stores a value with the given key in encrypted format using default secret key
     * @param key the key to store the value under
     * @param value the plain text value to encrypt and store
     */
    public static void setEncryptedValue(String key, String value) {
        setEncryptedValue(key, value, DEFAULT_SECRET_KEY);
    }
    
    /**
     * Stores a value with the given key in encrypted format using custom secret key
     * @param key the key to store the value under
     * @param value the plain text value to encrypt and store
     * @param secretKey the secret key to use for encryption
     */
    public static void setEncryptedValue(String key, String value, String secretKey) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        String encryptedValue = encrypt(value, secretKey);
        encryptedDataStore.put(key, encryptedValue);
    }
    
    /**
     * Retrieves the encrypted value for the given key
     * @param key the key to retrieve the encrypted value for
     * @return the encrypted value, or null if key doesn't exist
     */
    public static String getEncryptedValue(String key) {
        return encryptedDataStore.get(key);
    }
    
    /**
     * Retrieves and decrypts the value for the given key at runtime using default secret key
     * @param key the key to retrieve and decrypt the value for
     * @return the decrypted plain text value, or null if key doesn't exist
     */
    public static String getDecryptedValue(String key) {
        return getDecryptedValue(key, DEFAULT_SECRET_KEY);
    }
    
    /**
     * Retrieves and decrypts the value for the given key at runtime using custom secret key
     * @param key the key to retrieve and decrypt the value for
     * @param secretKey the secret key to use for decryption
     * @return the decrypted plain text value, or null if key doesn't exist
     */
    public static String getDecryptedValue(String key, String secretKey) {
        String encryptedValue = encryptedDataStore.get(key);
        if (encryptedValue == null) {
            return null;
        }
        return decrypt(encryptedValue, secretKey);
    }
    
    /**
     * Checks if a key exists in the encrypted data store
     * @param key the key to check
     * @return true if key exists, false otherwise
     */
    public static boolean containsKey(String key) {
        return encryptedDataStore.containsKey(key);
    }
    
    /**
     * Removes a key-value pair from the encrypted data store
     * @param key the key to remove
     * @return the encrypted value that was removed, or null if key didn't exist
     */
    public static String removeEncryptedValue(String key) {
        return encryptedDataStore.remove(key);
    }
    
    /**
     * Clears all stored encrypted values
     */
    public static void clearAll() {
        encryptedDataStore.clear();
    }
    
    /**
     * Gets the number of stored encrypted values
     * @return the size of the encrypted data store
     */
    public static int size() {
        return encryptedDataStore.size();
    }

} 