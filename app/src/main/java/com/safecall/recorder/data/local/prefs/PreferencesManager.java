package com.safecall.recorder.data.local.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * Manages encrypted shared preferences for app settings.
 * Uses EncryptedSharedPreferences for secure storage.
 */
@Singleton
public class PreferencesManager {

    private static final String PREFS_NAME = "safecall_prefs";
    private static final String KEY_ONBOARDING_COMPLETE = "onboarding_complete";
    private static final String KEY_CONSENT_ACCEPTED = "consent_accepted";
    private static final String KEY_AUTO_RECORDING = "auto_recording";
    private static final String KEY_DRIVE_BACKUP = "drive_backup";
    private static final String KEY_SCHEDULED_BACKUP = "scheduled_backup";
    private static final String KEY_WIFI_ONLY = "wifi_only";
    private static final String KEY_GOOGLE_EMAIL = "google_email";
    private static final String KEY_BEEP_TONE = "beep_tone";
    private static final String KEY_WHATSAPP_RECORDING = "whatsapp_recording";
    private static final String KEY_LAST_BACKUP = "last_backup";
    private static final String KEY_AUDIO_SOURCE = "audio_source";
    private static final String KEY_AUTO_SPEAKER = "auto_speaker";

    private final SharedPreferences prefs;

    @Inject
    public PreferencesManager(@ApplicationContext Context context) {
        SharedPreferences tempPrefs;
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            tempPrefs = EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException | IOException e) {
            // Fallback to regular SharedPreferences if encryption fails
            tempPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
        this.prefs = tempPrefs;
    }

    public boolean hasCompletedOnboarding() {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false);
    }

    public void setOnboardingComplete(boolean complete) {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETE, complete).apply();
    }

    public boolean hasAcceptedConsent() {
        return prefs.getBoolean(KEY_CONSENT_ACCEPTED, false);
    }

    public void setConsentAccepted(boolean accepted) {
        prefs.edit().putBoolean(KEY_CONSENT_ACCEPTED, accepted).apply();
    }

    public boolean isAutoRecordingEnabled() {
        return prefs.getBoolean(KEY_AUTO_RECORDING, true);
    }

    public void setAutoRecordingEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_AUTO_RECORDING, enabled).apply();
    }

    public boolean isDriveBackupEnabled() {
        return prefs.getBoolean(KEY_DRIVE_BACKUP, false);
    }

    public void setDriveBackupEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_DRIVE_BACKUP, enabled).apply();
    }

    public boolean isScheduledBackupEnabled() {
        return prefs.getBoolean(KEY_SCHEDULED_BACKUP, false);
    }

    public void setScheduledBackupEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SCHEDULED_BACKUP, enabled).apply();
    }

    public boolean isWifiOnlyBackup() {
        return prefs.getBoolean(KEY_WIFI_ONLY, true);
    }

    public void setWifiOnlyBackup(boolean wifiOnly) {
        prefs.edit().putBoolean(KEY_WIFI_ONLY, wifiOnly).apply();
    }

    public boolean isBiometricEnabled() {
        return prefs.getBoolean("biometric_enabled", false);
    }

    public void setBiometricEnabled(boolean enabled) {
        prefs.edit().putBoolean("biometric_enabled", enabled).apply();
    }

    public String getGoogleAccountEmail() {
        return prefs.getString(KEY_GOOGLE_EMAIL, null);
    }

    public void setGoogleAccountEmail(String email) {
        prefs.edit().putString(KEY_GOOGLE_EMAIL, email).apply();
    }

    public boolean shouldPlayBeepTone() {
        return prefs.getBoolean(KEY_BEEP_TONE, false);
    }

    public void setPlayBeepTone(boolean play) {
        prefs.edit().putBoolean(KEY_BEEP_TONE, play).apply();
    }

    public long getLastBackupTime() {
        return prefs.getLong(KEY_LAST_BACKUP, 0L);
    }

    public void setLastBackupTime(long time) {
        prefs.edit().putLong(KEY_LAST_BACKUP, time).apply();
    }

    public boolean isWhatsAppRecordingEnabled() {
        return prefs.getBoolean(KEY_WHATSAPP_RECORDING, false);
    }

    public void setWhatsAppRecordingEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_WHATSAPP_RECORDING, enabled).apply();
    }

    public int getAudioSource() {
        return prefs.getInt(KEY_AUDIO_SOURCE, android.media.MediaRecorder.AudioSource.MIC);
    }

    public void setAudioSource(int source) {
        prefs.edit().putInt(KEY_AUDIO_SOURCE, source).apply();
    }

    public boolean isAutoSpeakerEnabled() {
        return prefs.getBoolean(KEY_AUTO_SPEAKER, false);
    }

    public void setAutoSpeakerEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_AUTO_SPEAKER, enabled).apply();
    }

    public void clearAll() {
        prefs.edit().clear().apply();
    }
}
