package com.safecall.recorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.safecall.recorder.backup.BackupWorker;
import com.safecall.recorder.backup.DriveBackupManager;
import com.safecall.recorder.backup.GoogleSignInHelper;
import com.safecall.recorder.data.encryption.EncryptionManager;
import com.safecall.recorder.data.local.prefs.PreferencesManager;
import com.safecall.recorder.data.repository.RecordingRepository;
import com.safecall.recorder.databinding.ActivitySettingsBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Settings activity for app configuration.
 */
@AndroidEntryPoint
public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    @Inject
    PreferencesManager preferencesManager;

    @Inject
    GoogleSignInHelper googleSignInHelper;

    @Inject
    DriveBackupManager driveBackupManager;

    @Inject
    RecordingRepository repository;

    @Inject
    EncryptionManager encryptionManager;

    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize sign-in launcher
        signInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount account = task.getResult();
                            googleSignInHelper.handleSignInResult(account);
                            updateUI();
                            Toast.makeText(this, "Signed in successfully", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(this, "Sign-in failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        setupUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNotificationServiceEnabled() && preferencesManager.isWhatsAppRecordingEnabled()) {
            // Ensure switch is allowed to be checked
            binding.whatsappRecordingSwitch.setChecked(true);
        } else if (!isNotificationServiceEnabled() && binding.whatsappRecordingSwitch.isChecked()) {
            // User disabled permission externally
            binding.whatsappRecordingSwitch.setChecked(false);
            preferencesManager.setWhatsAppRecordingEnabled(false);
        }
    }

    private void setupUI() {
        // Toolbar
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // Auto recording switch
        binding.autoRecordingSwitch.setChecked(preferencesManager.isAutoRecordingEnabled());
        binding.autoRecordingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferencesManager.setAutoRecordingEnabled(isChecked);
        });

        // Audio Source Spinner
        String[] audioSources = {"Microphone (Default)", "Voice Communication", "Voice Recognition"};
        int[] audioSourceValues = {
                android.media.MediaRecorder.AudioSource.MIC,
                android.media.MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                android.media.MediaRecorder.AudioSource.VOICE_RECOGNITION
        };
        android.widget.ArrayAdapter<String> spinnerAdapter = new android.widget.ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, audioSources);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.audioSourceSpinner.setAdapter(spinnerAdapter);

        int currentSource = preferencesManager.getAudioSource();
        for (int i = 0; i < audioSourceValues.length; i++) {
            if (audioSourceValues[i] == currentSource) {
                binding.audioSourceSpinner.setSelection(i);
                break;
            }
        }
        binding.audioSourceSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                preferencesManager.setAudioSource(audioSourceValues[position]);
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Auto Speakerphone switch
        binding.autoSpeakerSwitch.setChecked(preferencesManager.isAutoSpeakerEnabled());
        binding.autoSpeakerSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferencesManager.setAutoSpeakerEnabled(isChecked);
        });

        // WhatsApp Recording switch
        binding.whatsappRecordingSwitch.setChecked(preferencesManager.isWhatsAppRecordingEnabled());
        binding.whatsappRecordingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!isNotificationServiceEnabled()) {
                    buttonView.setChecked(false); // Reset until enabled
                    new AlertDialog.Builder(this)
                            .setTitle("Permission Required")
                            .setMessage(
                                    "WhatsApp recording requires Notification Access to detect calls. Enable it in Settings?")
                            .setPositiveButton("Settings", (dialog, which) -> {
                                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                } else {
                    preferencesManager.setWhatsAppRecordingEnabled(true);
                }
            } else {
                preferencesManager.setWhatsAppRecordingEnabled(false);
            }
        });

        // Google account section
        updateUI();

        // Sign in / Sign out button
        binding.signInButton.setOnClickListener(v -> {
            if (googleSignInHelper.isSignedIn()) {
                googleSignInHelper.signOut(() -> runOnUiThread(this::updateUI));
            } else {
                signInLauncher.launch(googleSignInHelper.getSignInIntent());
            }
        });

        // Backup now button
        binding.backupNowButton.setOnClickListener(v -> {
            if (googleSignInHelper.isSignedIn()) {
                Toast.makeText(this, "Starting backup...", Toast.LENGTH_SHORT).show();
                driveBackupManager.backupAllUnbacked(count -> {
                    runOnUiThread(() -> {
                        if (count > 0) {
                            Toast.makeText(this, "Backed up " + count + " recording(s)", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "All recordings are already backed up", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            } else {
                Toast.makeText(this, "Please sign in first", Toast.LENGTH_SHORT).show();
            }
        });

        // Scheduled backup switch
        binding.scheduledBackupSwitch.setChecked(preferencesManager.isScheduledBackupEnabled());
        binding.scheduledBackupSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferencesManager.setScheduledBackupEnabled(isChecked);
            if (isChecked) {
                BackupWorker.schedulePeriodicBackup(this, preferencesManager.isWifiOnlyBackup(), 24);
            } else {
                BackupWorker.cancelPeriodicBackup(this);
            }
        });

        // Wi-Fi only switch
        binding.wifiOnlySwitch.setChecked(preferencesManager.isWifiOnlyBackup());
        binding.wifiOnlySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferencesManager.setWifiOnlyBackup(isChecked);
            if (preferencesManager.isScheduledBackupEnabled()) {
                BackupWorker.schedulePeriodicBackup(this, isChecked, 24);
            }
        });

        // App Security - Biometric Lock
        binding.biometricSwitch.setChecked(preferencesManager.isBiometricEnabled());
        binding.biometricSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Verify biometrics before enabling
                verifyBiometricsForEnable();
            } else {
                preferencesManager.setBiometricEnabled(false);
            }
        });

        // Clear data button
        binding.clearDataButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.clear_all_data)
                    .setMessage(R.string.clear_data_warning)
                    .setPositiveButton("Clear All", (dialog, which) -> {
                        repository.clearAllData();
                        encryptionManager.deleteKey();
                        preferencesManager.clearAll();
                        Toast.makeText(this, "All data cleared", Toast.LENGTH_SHORT).show();

                        // Restart app
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        });

        // Load storage stats
        loadStorageStats();
    }

    private void updateUI() {
        boolean signedIn = googleSignInHelper.isSignedIn();

        binding.googleAccountEmail.setText(signedIn ? preferencesManager.getGoogleAccountEmail() : "Not signed in");
        binding.signInButton.setText(signedIn ? R.string.sign_out : R.string.sign_in_google);

        binding.backupNowButton.setEnabled(signedIn);
        binding.scheduledBackupSwitch.setEnabled(signedIn);
        binding.wifiOnlySwitch.setEnabled(signedIn && preferencesManager.isScheduledBackupEnabled());
    }

    private void loadStorageStats() {
        new Thread(() -> {
            RecordingRepository.StorageStats stats = repository.getStorageStats();
            runOnUiThread(() -> {
                binding.storageInfo.setText(stats.recordingCount + " recordings • " +
                        formatStorageSize(stats.totalSizeBytes));
            });
        }).start();
    }

    private void verifyBiometricsForEnable() {
        if (androidx.biometric.BiometricManager.from(this)
                .canAuthenticate(
                        androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG) != androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(this, "Biometrics not available on this device", Toast.LENGTH_SHORT).show();
            binding.biometricSwitch.setChecked(false);
            return;
        }

        java.util.concurrent.Executor executor = androidx.core.content.ContextCompat.getMainExecutor(this);
        androidx.biometric.BiometricPrompt biometricPrompt = new androidx.biometric.BiometricPrompt(this,
                executor, new androidx.biometric.BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        binding.biometricSwitch.setChecked(false);
                        Toast.makeText(SettingsActivity.this, "Authentication failed: " + errString, Toast.LENGTH_SHORT)
                                .show();
                    }

                    @Override
                    public void onAuthenticationSucceeded(
                            @NonNull androidx.biometric.BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        preferencesManager.setBiometricEnabled(true);
                        Toast.makeText(SettingsActivity.this, "App Lock enabled", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(SettingsActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });

        androidx.biometric.BiometricPrompt.PromptInfo promptInfo = new androidx.biometric.BiometricPrompt.PromptInfo.Builder()
                .setTitle("Confirm Biometrics")
                .setSubtitle("Authenticate to enable App Lock")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private boolean isNotificationServiceEnabled() {
        String pkgName = getPackageName();
        final String flat = android.provider.Settings.Secure.getString(getContentResolver(),
                "enabled_notification_listeners");
        if (flat != null && !flat.isEmpty()) {
            final String[] names = flat.split(":");
            for (String name : names) {
                final android.content.ComponentName cn = android.content.ComponentName.unflattenFromString(name);
                if (cn != null) {
                    if (pkgName.equals(cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String formatStorageSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format(java.util.Locale.getDefault(), "%.1f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format(java.util.Locale.getDefault(), "%.1f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format(java.util.Locale.getDefault(), "%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
}
