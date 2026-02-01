package com.safecall.recorder.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.safecall.recorder.MainActivity;
import com.safecall.recorder.R;
import com.safecall.recorder.SafeCallApp;
import com.safecall.recorder.data.encryption.EncryptionManager;
import com.safecall.recorder.data.local.prefs.PreferencesManager;
import com.safecall.recorder.data.repository.RecordingRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Foreground service that handles call recording.
 * Implements multiple audio source strategies with automatic fallback.
 */
@AndroidEntryPoint
public class CallRecorderService extends Service {

    private static final String TAG = "CallRecorderService";

    public static final String ACTION_START_RECORDING = "com.safecall.recorder.START_RECORDING";
    public static final String ACTION_STOP_RECORDING = "com.safecall.recorder.STOP_RECORDING";
    public static final String EXTRA_PHONE_NUMBER = "phone_number";
    public static final String EXTRA_IS_INCOMING = "is_incoming";

    private static final int NOTIFICATION_ID = 1001;
    private static final int SAMPLE_RATE = 44100;

    @Inject
    RecordingRepository repository;

    @Inject
    EncryptionManager encryptionManager;

    @Inject
    PreferencesManager preferencesManager;

    private AudioRecord audioRecord;
    private volatile boolean isRecording = false;
    private File recordingFile;
    private long recordingStartTime;
    private String currentPhoneNumber = "";
    private boolean currentIsIncoming = false;
    private ExecutorService executor;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        executor = Executors.newSingleThreadExecutor();
        Log.d(TAG, "Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            Log.w(TAG, "Received null intent");
            return START_STICKY;
        }

        String action = intent.getAction();
        Log.d(TAG, "Received action: " + action);

        if (ACTION_START_RECORDING.equals(action)) {
            currentPhoneNumber = intent.getStringExtra(EXTRA_PHONE_NUMBER);
            if (currentPhoneNumber == null)
                currentPhoneNumber = "Unknown";
            currentIsIncoming = intent.getBooleanExtra(EXTRA_IS_INCOMING, false);
            Log.d(TAG, "Starting recording for: " + currentPhoneNumber + ", incoming: " + currentIsIncoming);
            startRecording();
        } else if (ACTION_STOP_RECORDING.equals(action)) {
            Log.d(TAG, "Stopping recording");
            stopRecording();
        }

        return START_STICKY;
    }

    /**
     * Start recording the call.
     */
    private void startRecording() {
        if (isRecording) {
            Log.w(TAG, "Already recording, ignoring start request");
            return;
        }

        // Start as foreground service with minimal notification
        try {
            startForeground(NOTIFICATION_ID, createNotification());
        } catch (Exception e) {
            Log.e(TAG, "Failed to start foreground service", e);
            stopSelf();
            return;
        }

        // Try different audio sources in order of preference
        int audioSource = findBestAudioSource();
        if (audioSource == -1) {
            Log.e(TAG, "No audio source available");
            stopSelf();
            return;
        }
        Log.d(TAG, "Using audio source: " + audioSource);

        try {
            int bufferSize = AudioRecord.getMinBufferSize(
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

            if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                Log.e(TAG, "Invalid buffer size: " + bufferSize);
                stopSelf();
                return;
            }

            Log.d(TAG, "Buffer size: " + bufferSize);

            audioRecord = new AudioRecord(
                    audioSource,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize * 2);

            if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                Log.e(TAG, "AudioRecord failed to initialize");
                stopSelf();
                return;
            }

            // Create recording file
            File recordingsDir = repository.getRecordingsDirectory();
            if (!recordingsDir.exists()) {
                recordingsDir.mkdirs();
            }

            String fileName = "recording_" + System.currentTimeMillis() + ".wav";
            recordingFile = new File(recordingsDir, fileName);
            Log.d(TAG, "Recording to: " + recordingFile.getAbsolutePath());

            recordingStartTime = System.currentTimeMillis();

            // IMPORTANT: Start recording BEFORE starting the write thread!
            audioRecord.startRecording();
            Log.d(TAG, "AudioRecord started");

            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> android.widget.Toast
                    .makeText(getApplicationContext(), "Recording Started", android.widget.Toast.LENGTH_SHORT).show());

            // Now that recording has started, set the flag and start writing
            isRecording = true;

            final int finalBufferSize = bufferSize;
            executor.execute(() -> writeAudioDataToFile(finalBufferSize));

        } catch (SecurityException e) {
            Log.e(TAG, "Security exception - missing permissions", e);
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> android.widget.Toast
                    .makeText(getApplicationContext(), "Error: Missing permissions", android.widget.Toast.LENGTH_LONG)
                    .show());
            stopSelf();
        } catch (Exception e) {
            Log.e(TAG, "Failed to start recording", e);
            new android.os.Handler(android.os.Looper.getMainLooper())
                    .post(() -> android.widget.Toast.makeText(getApplicationContext(),
                            "Recording Failed: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show());
            stopSelf();
        }
    }

    /**
     * Find the best available audio source for call recording.
     */
    private int findBestAudioSource() {
        int[] sourcesToTry = {
                MediaRecorder.AudioSource.VOICE_CALL,
                MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                MediaRecorder.AudioSource.VOICE_RECOGNITION,
                MediaRecorder.AudioSource.MIC
        };

        for (int source : sourcesToTry) {
            if (isAudioSourceAvailable(source)) {
                Log.d(TAG, "Audio source " + source + " is available");
                return source;
            }
        }

        return -1;
    }

    /**
     * Check if an audio source is available and working.
     */
    private boolean isAudioSourceAvailable(int source) {
        try {
            int bufferSize = AudioRecord.getMinBufferSize(
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

            if (bufferSize <= 0) {
                return false;
            }

            AudioRecord testRecord = new AudioRecord(
                    source,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize);

            boolean isAvailable = testRecord.getState() == AudioRecord.STATE_INITIALIZED;
            testRecord.release();
            return isAvailable;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Write audio data to WAV file.
     */
    private void writeAudioDataToFile(int bufferSize) {
        Log.d(TAG, "Starting audio write thread");
        byte[] buffer = new byte[bufferSize];
        long totalBytesWritten = 0;

        try (FileOutputStream output = new FileOutputStream(recordingFile)) {
            // Write WAV header placeholder (44 bytes)
            byte[] header = new byte[44];
            output.write(header);

            while (isRecording && audioRecord != null) {
                int bytesRead = audioRecord.read(buffer, 0, bufferSize);
                if (bytesRead > 0) {
                    output.write(buffer, 0, bytesRead);
                    totalBytesWritten += bytesRead;
                } else if (bytesRead == AudioRecord.ERROR_INVALID_OPERATION) {
                    Log.e(TAG, "AudioRecord read error: ERROR_INVALID_OPERATION");
                    break;
                } else if (bytesRead == AudioRecord.ERROR_BAD_VALUE) {
                    Log.e(TAG, "AudioRecord read error: ERROR_BAD_VALUE");
                    break;
                }
            }

            Log.d(TAG, "Audio write complete. Total bytes: " + totalBytesWritten);

        } catch (Exception e) {
            Log.e(TAG, "Error writing audio data", e);
        }

        // Update WAV header with correct file size
        if (recordingFile != null && recordingFile.exists()) {
            updateWavHeader(recordingFile, totalBytesWritten);
        }
    }

    /**
     * Update WAV file header with correct sizes.
     */
    private void updateWavHeader(File file, long dataSize) {
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            long totalSize = dataSize + 36;

            // RIFF chunk
            raf.writeBytes("RIFF");
            raf.writeInt(Integer.reverseBytes((int) totalSize));
            raf.writeBytes("WAVE");

            // fmt chunk
            raf.writeBytes("fmt ");
            raf.writeInt(Integer.reverseBytes(16));
            raf.writeShort(Short.reverseBytes((short) 1));
            raf.writeShort(Short.reverseBytes((short) 1));
            raf.writeInt(Integer.reverseBytes(SAMPLE_RATE));
            raf.writeInt(Integer.reverseBytes(SAMPLE_RATE * 2));
            raf.writeShort(Short.reverseBytes((short) 2));
            raf.writeShort(Short.reverseBytes((short) 16));

            // data chunk
            raf.writeBytes("data");
            raf.writeInt(Integer.reverseBytes((int) dataSize));

            Log.d(TAG, "WAV header updated. Data size: " + dataSize);
        } catch (Exception e) {
            Log.e(TAG, "Error updating WAV header", e);
        }
    }

    /**
     * Stop recording and save to database.
     */
    private void stopRecording() {
        if (!isRecording) {
            Log.w(TAG, "Not recording, ignoring stop request");
            stopForeground(STOP_FOREGROUND_REMOVE);
            stopSelf();
            return;
        }

        Log.d(TAG, "Stopping recording...");
        isRecording = false;

        if (audioRecord != null) {
            try {
                audioRecord.stop();
                audioRecord.release();
                Log.d(TAG, "AudioRecord stopped and released");
            } catch (Exception e) {
                Log.e(TAG, "Error stopping AudioRecord", e);
            }
            audioRecord = null;
        }

        final long duration = System.currentTimeMillis() - recordingStartTime;
        final String phoneNumber = currentPhoneNumber;
        final boolean isIncoming = currentIsIncoming;
        final File file = recordingFile;

        Log.d(TAG, "Recording duration: " + duration + "ms");

        // Encrypt and save recording in background
        executor.execute(() -> {
            try {
                if (file != null && file.exists()) {
                    long fileSize = file.length();
                    Log.d(TAG, "Recording file size: " + fileSize + " bytes");

                    if (fileSize > 44) { // More than just the header
                        // Encrypt the file
                        Log.d(TAG, "Encrypting file...");
                        File encryptedFile = encryptionManager.encryptFile(file);
                        Log.d(TAG, "File encrypted: " + encryptedFile.getAbsolutePath());

                        // Save to database
                        Log.d(TAG, "Saving to database...");
                        long id = repository.saveRecording(
                                encryptedFile.getAbsolutePath(),
                                phoneNumber,
                                isIncoming,
                                duration);
                        Log.d(TAG, "Recording saved with ID: " + id);
                    } else {
                        Log.w(TAG, "Recording file too small, deleting");
                        file.delete();
                    }
                } else {
                    Log.w(TAG, "Recording file is null or doesn't exist");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error saving recording", e);
                if (file != null && file.exists()) {
                    file.delete();
                }
            } finally {
                stopForeground(STOP_FOREGROUND_REMOVE);
                stopSelf();
            }
        });
    }

    /**
     * Create minimal notification for foreground service.
     */
    private Notification createNotification() {
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                new Intent(this, MainActivity.class),
                PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, SafeCallApp.RECORDING_CHANNEL_ID)
                .setContentTitle(getString(R.string.recording_notification_title))
                .setContentText(getString(R.string.recording_notification_text))
                .setSmallIcon(android.R.drawable.ic_btn_speak_now)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setSilent(true)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
        isRecording = false;
        if (audioRecord != null) {
            try {
                audioRecord.release();
            } catch (Exception e) {
                Log.e(TAG, "Error releasing AudioRecord", e);
            }
        }
        if (executor != null) {
            executor.shutdown();
        }
    }
}
