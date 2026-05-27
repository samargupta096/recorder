package com.safecall.recorder;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityManager;
import android.provider.Settings;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.safecall.recorder.backup.BackupWorker;
import com.safecall.recorder.backup.DriveBackupManager;
import com.safecall.recorder.backup.GoogleSignInHelper;
import com.safecall.recorder.data.encryption.EncryptionManager;
import com.safecall.recorder.data.local.db.RecordingEntity;
import com.safecall.recorder.data.local.prefs.PreferencesManager;
import com.safecall.recorder.data.repository.RecordingRepository;
import com.safecall.recorder.databinding.ActivityMainBinding;
import com.safecall.recorder.ui.RecordingsAdapter;

import android.widget.TextView;
import android.os.Handler;
import android.os.Looper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
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
    private static final String[] REQUIRED_PERMISSIONS;
    static {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            REQUIRED_PERMISSIONS = new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.POST_NOTIFICATIONS
            };
        } else {
            REQUIRED_PERMISSIONS = new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_CALL_LOG
            };
        }
    }

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

    @Inject
    EncryptionManager encryptionManager;

    private ActivityResultLauncher<Intent> signInLauncher;

    private boolean showingRecycleBin = false;
    private LiveData<List<RecordingEntity>> currentRecordingsLiveData;

    private MediaRecorder mediaRecorder;
    private boolean isManualRecording = false;
    private boolean isRecordingPaused = false;
    private File currentRecordingFile;
    private long recordingStartTime;
    private long totalRecordingDuration = 0;

    private BottomSheetDialog recordingDialog;
    private TextView tvRecordingTimer;
    private FloatingActionButton fabPauseResume;
    
    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isManualRecording && !isRecordingPaused) {
                long currentDuration = totalRecordingDuration + (System.currentTimeMillis() - recordingStartTime);
                updateTimerUI(currentDuration);
                timerHandler.postDelayed(this, 500);
            }
        }
    };

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

        binding.fabRecord.setOnClickListener(v -> {
            showRecordingStudio();
        });

        observeRecordings();

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
            } else if (itemId == R.id.action_recycle_bin) {
                showingRecycleBin = !showingRecycleBin;
                observeRecordings();
                item.setTitle(showingRecycleBin ? "Exit Recycle Bin" : "Recycle Bin");
                return true;
            }
            return false;
        });

        updateUI();
        checkAccessibilityService();
    }

    private void checkAccessibilityService() {
        if (!isAccessibilityServiceEnabled()) {
            new AlertDialog.Builder(this)
                .setTitle("Accessibility Permission Required")
                .setMessage("To record calls reliably on modern Android devices, SafeCall needs Accessibility Service permission. Please enable it in the next screen.")
                .setPositiveButton("Enable", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
        }
    }

    private boolean isAccessibilityServiceEnabled() {
        AccessibilityManager am = (AccessibilityManager) getSystemService(android.content.Context.ACCESSIBILITY_SERVICE);
        if (am == null) return false;
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
        for (AccessibilityServiceInfo service : enabledServices) {
            if (service.getId().contains("CallRecordingAccessibilityService")) {
                return true;
            }
        }
        return false;
    }

    private void observeRecordings() {
        if (currentRecordingsLiveData != null) {
            currentRecordingsLiveData.removeObservers(this);
        }
        
        if (showingRecycleBin) {
            binding.toolbar.setTitle("Recycle Bin");
            currentRecordingsLiveData = repository.getDeletedRecordings();
        } else {
            binding.toolbar.setTitle(R.string.app_name);
            currentRecordingsLiveData = repository.getAllRecordings();
        }
        
        currentRecordingsLiveData.observe(this, recordings -> {
            adapter.submitList(recordings);
            binding.emptyState.setVisibility(recordings.isEmpty() ? View.VISIBLE : View.GONE);
            binding.recyclerView.setVisibility(recordings.isEmpty() ? View.GONE : View.VISIBLE);
        });
    }

    private void showRecordingDetails(RecordingEntity recording) {
        Intent intent = new Intent(this, RecordingDetailsActivity.class);
        intent.putExtra("recording_id", recording.getId());
        startActivity(intent);
    }

    private void showDeleteConfirmation(RecordingEntity recording) {
        if (showingRecycleBin) {
            new AlertDialog.Builder(this)
                    .setTitle("Recycle Bin")
                    .setMessage("Do you want to restore or permanently delete?")
                    .setPositiveButton("Restore", (dialog, which) -> {
                        repository.restoreRecording(recording);
                    })
                    .setNegativeButton("Delete", (dialog, which) -> {
                        repository.deleteRecording(recording);
                    })
                    .setNeutralButton("Cancel", null)
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.delete)
                    .setMessage(R.string.delete_confirmation)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        repository.deleteRecording(recording);
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        }
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

    private void showRecordingStudio() {
        if (recordingDialog == null) {
            recordingDialog = new BottomSheetDialog(this);
            View view = getLayoutInflater().inflate(R.layout.layout_recording_bottom_sheet, null);
            recordingDialog.setContentView(view);
            
            // Prevent dismissing the dialog while recording by touching outside
            recordingDialog.setCancelable(false);

            tvRecordingTimer = view.findViewById(R.id.tv_recording_timer);
            fabPauseResume = view.findViewById(R.id.fab_pause_resume);
            FloatingActionButton fabStopSave = view.findViewById(R.id.fab_stop_save);
            View btnCancel = view.findViewById(R.id.btn_cancel_recording);

            fabPauseResume.setOnClickListener(v -> togglePauseResume());
            fabStopSave.setOnClickListener(v -> stopAndSaveRecording());
            btnCancel.setOnClickListener(v -> cancelRecording());
        }

        recordingDialog.show();
        startManualRecording();
    }

    private void startManualRecording() {
        File recordingsDir = repository.getRecordingsDirectory();
        currentRecordingFile = new File(recordingsDir, "manual_" + System.currentTimeMillis() + ".wav");

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(currentRecordingFile.getAbsolutePath());

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isManualRecording = true;
            isRecordingPaused = false;
            totalRecordingDuration = 0;
            recordingStartTime = System.currentTimeMillis();
            
            updateTimerUI(0);
            timerHandler.postDelayed(timerRunnable, 500);
            
            fabPauseResume.setImageResource(R.drawable.ic_pause);
        } catch (IOException e) {
            Toast.makeText(this, "Failed to start recording", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            recordingDialog.dismiss();
        }
    }

    private void togglePauseResume() {
        if (!isManualRecording || mediaRecorder == null) return;

        if (isRecordingPaused) {
            // Resume
            try {
                mediaRecorder.resume();
                isRecordingPaused = false;
                recordingStartTime = System.currentTimeMillis();
                fabPauseResume.setImageResource(R.drawable.ic_pause);
                timerHandler.postDelayed(timerRunnable, 500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Pause
            try {
                mediaRecorder.pause();
                isRecordingPaused = true;
                totalRecordingDuration += (System.currentTimeMillis() - recordingStartTime);
                fabPauseResume.setImageResource(R.drawable.ic_play); // Wait, do we have ic_play? Yes, ic_play.xml exists
                timerHandler.removeCallbacks(timerRunnable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void stopAndSaveRecording() {
        stopMediaRecorder();
        
        long currentDuration = totalRecordingDuration;
        if (!isRecordingPaused) {
            currentDuration += (System.currentTimeMillis() - recordingStartTime);
        }
        final long finalDuration = currentDuration;

        new Thread(() -> {
            try {
                if (currentRecordingFile != null && currentRecordingFile.exists()) {
                    File encryptedFile = encryptionManager.encryptFile(currentRecordingFile);
                    repository.saveRecording(encryptedFile.getAbsolutePath(), "Voice Memo", false, finalDuration);
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Recording saved", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to save recording", Toast.LENGTH_SHORT).show());
            }
        }).start();

        if (recordingDialog != null && recordingDialog.isShowing()) {
            recordingDialog.dismiss();
        }
    }

    private void cancelRecording() {
        stopMediaRecorder();
        if (currentRecordingFile != null && currentRecordingFile.exists()) {
            currentRecordingFile.delete();
        }
        if (recordingDialog != null && recordingDialog.isShowing()) {
            recordingDialog.dismiss();
        }
    }

    private void stopMediaRecorder() {
        timerHandler.removeCallbacks(timerRunnable);
        if (mediaRecorder != null) {
            try {
                if (isManualRecording && !isRecordingPaused) {
                    totalRecordingDuration += (System.currentTimeMillis() - recordingStartTime);
                }
                mediaRecorder.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mediaRecorder.release();
            mediaRecorder = null;
        }
        isManualRecording = false;
        isRecordingPaused = false;
    }

    private void updateTimerUI(long durationMs) {
        if (tvRecordingTimer == null) return;
        int seconds = (int) (durationMs / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;

        String timeText;
        if (hours > 0) {
            timeText = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeText = String.format("%02d:%02d", minutes, seconds);
        }
        tvRecordingTimer.setText(timeText);
    }
}
