package com.safecall.recorder;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.safecall.recorder.backup.BackupWorker;
import com.safecall.recorder.backup.DriveBackupManager;
import com.safecall.recorder.backup.GoogleSignInHelper;
import com.safecall.recorder.data.local.db.RecordingEntity;
import com.safecall.recorder.data.local.prefs.PreferencesManager;
import com.safecall.recorder.data.repository.RecordingRepository;
import com.safecall.recorder.databinding.ActivityMainBinding;
import com.safecall.recorder.ui.RecordingsAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Main activity displaying list of recordings.
 */
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG
    };

    private ActivityMainBinding binding;
    private RecordingsAdapter adapter;

    @Inject
    PreferencesManager preferencesManager;

    @Inject
    RecordingRepository repository;

    @Inject
    GoogleSignInHelper googleSignInHelper;

    @Inject
    DriveBackupManager driveBackupManager;

    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
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

        // Check if onboarding needed
        if (!preferencesManager.hasCompletedOnboarding()) {
            showOnboarding();
        } else {
            // Check for biometric lock
            if (preferencesManager.isBiometricEnabled()) {
                authenticateUser();
            } else {
                setupMainUI();
            }
        }
    }

    private void authenticateUser() {
        if (androidx.biometric.BiometricManager.from(this)
                .canAuthenticate(
                        androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG) != androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS) {
            setupMainUI(); // Fallback if biometrics unavailable
            return;
        }

        java.util.concurrent.Executor executor = androidx.core.content.ContextCompat.getMainExecutor(this);
        androidx.biometric.BiometricPrompt biometricPrompt = new androidx.biometric.BiometricPrompt(this,
                executor, new androidx.biometric.BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        finish(); // Close app on error or cancellation
                    }

                    @Override
                    public void onAuthenticationSucceeded(
                            @NonNull androidx.biometric.BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        setupMainUI();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        // Prompt remains shown
                    }
                });

        androidx.biometric.BiometricPrompt.PromptInfo promptInfo = new androidx.biometric.BiometricPrompt.PromptInfo.Builder()
                .setTitle("SafeCall Locked")
                .setSubtitle("Authenticate to access recordings")
                .setNegativeButtonText("Exit")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void showOnboarding() {
        // Show onboarding dialog
        new AlertDialog.Builder(this)
                .setTitle(R.string.onboarding_title)
                .setMessage(R.string.consent_text)
                .setPositiveButton(R.string.get_started, (dialog, which) -> {
                    preferencesManager.setConsentAccepted(true);
                    preferencesManager.setOnboardingComplete(true);
                    preferencesManager.setAutoRecordingEnabled(true);
                    checkPermissions();
                })
                .setCancelable(false)
                .show();
    }

    private void checkPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission);
            }
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsNeeded.toArray(new String[0]),
                    PERMISSION_REQUEST_CODE);
        } else {
            setupMainUI();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                setupMainUI();
            } else {
                Toast.makeText(this, R.string.error_permission_denied, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setupMainUI() {
        // Setup RecyclerView
        adapter = new RecordingsAdapter(this, recording -> {
            // Open details
            showRecordingDetails(recording);
        }, recording -> {
            // Delete
            showDeleteConfirmation(recording);
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        // Observe recordings
        repository.getAllRecordings().observe(this, recordings -> {
            adapter.submitList(recordings);
            binding.emptyState.setVisibility(recordings.isEmpty() ? View.VISIBLE : View.GONE);
            binding.recyclerView.setVisibility(recordings.isEmpty() ? View.GONE : View.VISIBLE);
        });

        // Setup toolbar menu
        binding.toolbar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_settings) {
                showSettings();
                return true;
            } else if (itemId == R.id.action_backup) {
                if (googleSignInHelper.isSignedIn()) {
                    performBackup();
                } else {
                    signInLauncher.launch(googleSignInHelper.getSignInIntent());
                }
                return true;
            }
            return false;
        });

        updateUI();
    }

    private void showRecordingDetails(RecordingEntity recording) {
        Intent intent = new Intent(this, RecordingDetailsActivity.class);
        intent.putExtra("recording_id", recording.getId());
        startActivity(intent);
    }

    private void showDeleteConfirmation(RecordingEntity recording) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setMessage(R.string.delete_confirmation)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    repository.deleteRecording(recording);
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void performBackup() {
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
    }

    private void updateUI() {
        // Update any UI elements based on current state
    }
}
