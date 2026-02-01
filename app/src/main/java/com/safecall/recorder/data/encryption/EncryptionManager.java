package com.safecall.recorder.data.encryption;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * Manages AES-256-GCM encryption of recording files using Android Keystore.
 * Provides secure key storage and authenticated encryption.
 */
@Singleton
public class EncryptionManager {

    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String KEY_ALIAS = "safecall_recording_key";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int BUFFER_SIZE = 8192;

    private final Context context;
    private KeyStore keyStore;

    @Inject
    public EncryptionManager(@ApplicationContext Context context) {
        this.context = context;
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get or create the encryption key from Android Keystore.
     */
    private SecretKey getOrCreateKey() throws Exception {
        if (keyStore.containsAlias(KEY_ALIAS)) {
            KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) keyStore.getEntry(KEY_ALIAS, null);
            return entry.getSecretKey();
        }

        KeyGenerator keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
        );

        KeyGenParameterSpec keySpec = new KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
        )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setUserAuthenticationRequired(false)
                .build();

        keyGenerator.init(keySpec);
        return keyGenerator.generateKey();
    }

    /**
     * Encrypt a recording file in place.
     *
     * @param inputFile The unencrypted recording file
     * @return The encrypted file (same path with .enc extension)
     */
    public File encryptFile(File inputFile) throws Exception {
        SecretKey key = getOrCreateKey();
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] iv = cipher.getIV();
        File outputFile = new File(inputFile.getParent(), 
                inputFile.getName().replace(".wav", "") + ".enc");

        try (FileInputStream input = new FileInputStream(inputFile);
             FileOutputStream output = new FileOutputStream(outputFile)) {

            // Write IV length and IV at the beginning
            output.write(iv.length);
            output.write(iv);

            // Encrypt and write data in chunks
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;

            while ((bytesRead = input.read(buffer)) != -1) {
                byte[] encryptedChunk = cipher.update(buffer, 0, bytesRead);
                if (encryptedChunk != null) {
                    output.write(encryptedChunk);
                }
            }

            // Write final block
            byte[] finalBlock = cipher.doFinal();
            if (finalBlock != null) {
                output.write(finalBlock);
            }
        }

        // Delete original unencrypted file
        inputFile.delete();

        return outputFile;
    }

    /**
     * Decrypt a recording file to a temporary location for playback.
     *
     * @param encryptedFile The encrypted recording file
     * @return Temporary decrypted file for playback
     */
    public File decryptFile(File encryptedFile) throws Exception {
        SecretKey key = getOrCreateKey();
        File tempFile = new File(context.getCacheDir(), "temp_" + System.currentTimeMillis() + ".wav");

        try (FileInputStream input = new FileInputStream(encryptedFile);
             FileOutputStream output = new FileOutputStream(tempFile)) {

            // Read IV
            int ivLength = input.read();
            byte[] iv = new byte[ivLength];
            input.read(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

            // Read all encrypted data and decrypt
            byte[] encryptedData = new byte[(int) (encryptedFile.length() - ivLength - 1)];
            input.read(encryptedData);

            byte[] decryptedData = cipher.doFinal(encryptedData);
            output.write(decryptedData);
        }

        // Mark for deletion on exit
        tempFile.deleteOnExit();

        return tempFile;
    }

    /**
     * Check if encryption key exists in Keystore.
     */
    public boolean isKeyAvailable() {
        try {
            return keyStore.containsAlias(KEY_ALIAS);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Delete the encryption key (for clear all data feature).
     * WARNING: This will make all encrypted recordings unreadable!
     */
    public void deleteKey() {
        try {
            if (keyStore.containsAlias(KEY_ALIAS)) {
                keyStore.deleteEntry(KEY_ALIAS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
