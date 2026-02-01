package com.safecall.recorder.data.repository;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.lifecycle.LiveData;

import com.safecall.recorder.data.encryption.EncryptionManager;
import com.safecall.recorder.data.local.db.RecordingDao;
import com.safecall.recorder.data.local.db.RecordingEntity;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * Repository for managing call recordings.
 * Handles database operations, file management, and contact resolution.
 */
@Singleton
public class RecordingRepository {

    private static final String RECORDINGS_DIR = "recordings";

    private final RecordingDao recordingDao;
    private final EncryptionManager encryptionManager;
    private final Context context;
    private final ExecutorService executor;

    @Inject
    public RecordingRepository(
            RecordingDao recordingDao,
            EncryptionManager encryptionManager,
            @ApplicationContext Context context) {
        this.recordingDao = recordingDao;
        this.encryptionManager = encryptionManager;
        this.context = context;
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Get all recordings as LiveData for reactive updates.
     */
    public LiveData<List<RecordingEntity>> getAllRecordings() {
        return recordingDao.getAllRecordings();
    }

    /**
     * Search recordings by query.
     */
    public LiveData<List<RecordingEntity>> searchRecordings(String query) {
        return recordingDao.searchRecordings(query);
    }

    /**
     * Get a single recording by ID.
     */
    public RecordingEntity getRecordingById(long id) {
        return recordingDao.getRecordingById(id);
    }

    /**
     * Get a single recording by ID as LiveData.
     */
    public LiveData<RecordingEntity> getRecordingByIdLive(long id) {
        return recordingDao.getRecordingByIdLive(id);
    }

    /**
     * Get recordings that haven't been backed up.
     */
    public List<RecordingEntity> getUnbackedRecordings() {
        return recordingDao.getUnbackedRecordings();
    }

    /**
     * Save a new recording to the database.
     */
    public long saveRecording(String filePath, String phoneNumber, boolean isIncoming, long duration) {
        File file = new File(filePath);
        String contactName = getContactName(phoneNumber);

        RecordingEntity recording = new RecordingEntity();
        recording.setFilePath(filePath);
        recording.setContactName(contactName);
        recording.setPhoneNumber(phoneNumber);
        recording.setIncoming(isIncoming);
        recording.setTimestamp(System.currentTimeMillis());
        recording.setDuration(duration);
        recording.setFileSize(file.length());
        recording.setEncrypted(filePath.endsWith(".enc"));

        return recordingDao.insertRecording(recording);
    }

    /**
     * Delete a recording (both file and database entry).
     */
    public void deleteRecording(RecordingEntity recording) {
        executor.execute(() -> {
            File file = new File(recording.getFilePath());
            if (file.exists()) {
                file.delete();
            }
            recordingDao.deleteRecording(recording);
        });
    }

    /**
     * Rename a recording.
     */
    public void renameRecording(long id, String newName) {
        executor.execute(() -> recordingDao.updateCustomName(id, newName));
    }

    /**
     * Toggle favorite status.
     */
    public void toggleFavorite(long id, boolean isFavorite) {
        executor.execute(() -> recordingDao.updateFavoriteStatus(id, isFavorite));
    }

    /**
     * Update notes.
     */
    public void updateNotes(long id, String notes) {
        executor.execute(() -> recordingDao.updateNotes(id, notes));
    }

    /**
     * Mark a recording as backed up.
     */
    public void markAsBackedUp(long id, String driveFileId) {
        executor.execute(() -> recordingDao.markAsBackedUp(id, driveFileId));
    }

    /**
     * Get decrypted file for playback.
     */
    public File getDecryptedFileForPlayback(RecordingEntity recording) throws Exception {
        File encryptedFile = new File(recording.getFilePath());
        if (recording.isEncrypted() && encryptedFile.exists()) {
            return encryptionManager.decryptFile(encryptedFile);
        } else {
            return encryptedFile;
        }
    }

    /**
     * Get File for sharing (creates decrypted copy).
     */
    public File getFileForSharing(RecordingEntity recording) throws Exception {
        File file = new File(recording.getFilePath());
        if (recording.isEncrypted()) {
            return encryptionManager.decryptFile(file);
        } else {
            return file;
        }
    }

    /**
     * Get the recordings directory.
     */
    public File getRecordingsDirectory() {
        File dir = new File(context.getFilesDir(), RECORDINGS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * Get storage statistics.
     */
    public StorageStats getStorageStats() {
        int count = recordingDao.getRecordingCount();
        long totalSize = recordingDao.getTotalStorageUsed();
        return new StorageStats(count, totalSize);
    }

    /**
     * Clear all data (recordings and files).
     */
    public void clearAllData() {
        executor.execute(() -> {
            // Delete all files
            File recordingsDir = getRecordingsDirectory();
            File[] files = recordingsDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }

            // Clear database
            recordingDao.deleteAllRecordings();

            // Clear temp files
            File cacheDir = context.getCacheDir();
            File[] cacheFiles = cacheDir.listFiles();
            if (cacheFiles != null) {
                for (File file : cacheFiles) {
                    if (file.getName().startsWith("temp_")) {
                        file.delete();
                    }
                }
            }
        });
    }

    /**
     * Resolve contact name from phone number.
     */
    private String getContactName(String phoneNumber) {
        try {
            Uri uri = Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(phoneNumber));

            Cursor cursor = context.getContentResolver().query(
                    uri,
                    new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME },
                    null,
                    null,
                    null);

            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        return cursor.getString(0);
                    }
                } finally {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            // Ignore errors
        }
        return null;
    }

    /**
     * Storage statistics data class.
     */
    public static class StorageStats {
        public final int recordingCount;
        public final long totalSizeBytes;

        public StorageStats(int recordingCount, long totalSizeBytes) {
            this.recordingCount = recordingCount;
            this.totalSizeBytes = totalSizeBytes;
        }
    }
}
