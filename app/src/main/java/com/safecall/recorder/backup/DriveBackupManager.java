package com.safecall.recorder.backup;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.safecall.recorder.data.encryption.EncryptionManager;
import com.safecall.recorder.data.local.db.RecordingEntity;
import com.safecall.recorder.data.local.prefs.PreferencesManager;
import com.safecall.recorder.data.repository.RecordingRepository;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * Manages Google Drive backup operations for recordings.
 */
@Singleton
public class DriveBackupManager {

    private static final String APP_FOLDER_NAME = "SafeCallRecorder";
    private static final String MIME_TYPE_FOLDER = "application/vnd.google-apps.folder";
    private static final String MIME_TYPE_AUDIO = "audio/wav";

    private final Context context;
    private final GoogleSignInHelper googleSignInHelper;
    private final RecordingRepository repository;
    private final EncryptionManager encryptionManager;
    private final PreferencesManager preferencesManager;
    private final ExecutorService executor;

    @Inject
    public DriveBackupManager(
            @ApplicationContext Context context,
            GoogleSignInHelper googleSignInHelper,
            RecordingRepository repository,
            EncryptionManager encryptionManager,
            PreferencesManager preferencesManager
    ) {
        this.context = context;
        this.googleSignInHelper = googleSignInHelper;
        this.repository = repository;
        this.encryptionManager = encryptionManager;
        this.preferencesManager = preferencesManager;
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Get Drive service instance for the signed-in account.
     */
    private Drive getDriveService() {
        GoogleSignInAccount account = googleSignInHelper.getSignedInAccount();
        if (account == null) return null;

        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                context,
                Collections.singletonList(DriveScopes.DRIVE_FILE)
        );
        credential.setSelectedAccount(account.getAccount());

        return new Drive.Builder(
                new NetHttpTransport(),
                new GsonFactory(),
                credential
        )
                .setApplicationName("SafeCall Recorder")
                .build();
    }

    /**
     * Get or create the app folder in Drive.
     */
    private String getOrCreateAppFolder(Drive drive) throws Exception {
        String query = "name='" + APP_FOLDER_NAME + "' and mimeType='" + MIME_TYPE_FOLDER + "' and trashed=false";
        FileList result = drive.files().list()
                .setQ(query)
                .setSpaces("drive")
                .execute();

        if (!result.getFiles().isEmpty()) {
            return result.getFiles().get(0).getId();
        }

        File folderMetadata = new File();
        folderMetadata.setName(APP_FOLDER_NAME);
        folderMetadata.setMimeType(MIME_TYPE_FOLDER);

        File folder = drive.files().create(folderMetadata)
                .setFields("id")
                .execute();

        return folder.getId();
    }

    /**
     * Backup a single recording to Google Drive.
     *
     * @param recording The recording to backup
     * @param callback  Callback with the Drive file ID if successful
     */
    public void backupRecording(RecordingEntity recording, BackupCallback callback) {
        if (recording.isBackedUp()) {
            if (callback != null) callback.onSuccess(recording.getDriveFileId());
            return;
        }

        executor.execute(() -> {
            try {
                Drive drive = getDriveService();
                if (drive == null) {
                    if (callback != null) callback.onError("Not signed in");
                    return;
                }

                String folderId = getOrCreateAppFolder(drive);

                // Decrypt file for upload
                java.io.File encryptedFile = new java.io.File(recording.getFilePath());
                java.io.File decryptedFile;
                if (recording.isEncrypted()) {
                    decryptedFile = encryptionManager.decryptFile(encryptedFile);
                } else {
                    decryptedFile = encryptedFile;
                }

                // Create file metadata
                String displayName = recording.getDisplayName();
                String fileName = displayName + "_" + recording.getTimestamp() + ".wav";

                File fileMetadata = new File();
                fileMetadata.setName(fileName);
                fileMetadata.setParents(Collections.singletonList(folderId));

                // Upload file
                FileContent mediaContent = new FileContent(MIME_TYPE_AUDIO, decryptedFile);
                File uploadedFile = drive.files().create(fileMetadata, mediaContent)
                        .setFields("id")
                        .execute();

                // Clean up temp decrypted file
                if (recording.isEncrypted()) {
                    decryptedFile.delete();
                }

                // Mark as backed up in database
                repository.markAsBackedUp(recording.getId(), uploadedFile.getId());
                preferencesManager.setLastBackupTime(System.currentTimeMillis());

                if (callback != null) callback.onSuccess(uploadedFile.getId());

            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Backup all unbacked recordings.
     *
     * @param callback Callback with the number of backed up recordings
     */
    public void backupAllUnbacked(BackupAllCallback callback) {
        executor.execute(() -> {
            if (!googleSignInHelper.isSignedIn()) {
                if (callback != null) callback.onComplete(0);
                return;
            }

            List<RecordingEntity> unbacked = repository.getUnbackedRecordings();
            int successCount = 0;

            for (RecordingEntity recording : unbacked) {
                try {
                    Drive drive = getDriveService();
                    if (drive == null) break;

                    String folderId = getOrCreateAppFolder(drive);

                    java.io.File encryptedFile = new java.io.File(recording.getFilePath());
                    java.io.File decryptedFile;
                    if (recording.isEncrypted()) {
                        decryptedFile = encryptionManager.decryptFile(encryptedFile);
                    } else {
                        decryptedFile = encryptedFile;
                    }

                    String displayName = recording.getDisplayName();
                    String fileName = displayName + "_" + recording.getTimestamp() + ".wav";

                    File fileMetadata = new File();
                    fileMetadata.setName(fileName);
                    fileMetadata.setParents(Collections.singletonList(folderId));

                    FileContent mediaContent = new FileContent(MIME_TYPE_AUDIO, decryptedFile);
                    File uploadedFile = drive.files().create(fileMetadata, mediaContent)
                            .setFields("id")
                            .execute();

                    if (recording.isEncrypted()) {
                        decryptedFile.delete();
                    }

                    repository.markAsBackedUp(recording.getId(), uploadedFile.getId());
                    successCount++;

                } catch (Exception e) {
                    // Continue with next recording
                }
            }

            preferencesManager.setLastBackupTime(System.currentTimeMillis());
            final int count = successCount;
            if (callback != null) callback.onComplete(count);
        });
    }

    /**
     * List all backups in the Drive folder.
     */
    public void listBackups(ListBackupsCallback callback) {
        executor.execute(() -> {
            try {
                Drive drive = getDriveService();
                if (drive == null) {
                    if (callback != null) callback.onComplete(new ArrayList<>());
                    return;
                }

                String folderId = getOrCreateAppFolder(drive);
                String query = "'" + folderId + "' in parents and trashed=false";

                FileList result = drive.files().list()
                        .setQ(query)
                        .setFields("files(id, name, size, createdTime)")
                        .execute();

                List<DriveBackupInfo> backups = new ArrayList<>();
                for (File file : result.getFiles()) {
                    backups.add(new DriveBackupInfo(
                            file.getId(),
                            file.getName(),
                            file.getSize() != null ? file.getSize() : 0L,
                            file.getCreatedTime() != null ? file.getCreatedTime().getValue() : 0L
                    ));
                }

                if (callback != null) callback.onComplete(backups);

            } catch (Exception e) {
                if (callback != null) callback.onComplete(new ArrayList<>());
            }
        });
    }

    /**
     * Download a backup from Drive.
     */
    public void downloadBackup(String driveFileId, java.io.File destFile, DownloadCallback callback) {
        executor.execute(() -> {
            try {
                Drive drive = getDriveService();
                if (drive == null) {
                    if (callback != null) callback.onComplete(false);
                    return;
                }

                try (FileOutputStream output = new FileOutputStream(destFile)) {
                    drive.files().get(driveFileId)
                            .executeMediaAndDownloadTo(output);
                }

                if (callback != null) callback.onComplete(true);

            } catch (Exception e) {
                if (callback != null) callback.onComplete(false);
            }
        });
    }

    /**
     * Delete a backup from Drive.
     */
    public void deleteBackup(String driveFileId, DeleteCallback callback) {
        executor.execute(() -> {
            try {
                Drive drive = getDriveService();
                if (drive == null) {
                    if (callback != null) callback.onComplete(false);
                    return;
                }

                drive.files().delete(driveFileId).execute();
                if (callback != null) callback.onComplete(true);

            } catch (Exception e) {
                if (callback != null) callback.onComplete(false);
            }
        });
    }

    // Callback interfaces
    public interface BackupCallback {
        void onSuccess(String driveFileId);
        void onError(String message);
    }

    public interface BackupAllCallback {
        void onComplete(int count);
    }

    public interface ListBackupsCallback {
        void onComplete(List<DriveBackupInfo> backups);
    }

    public interface DownloadCallback {
        void onComplete(boolean success);
    }

    public interface DeleteCallback {
        void onComplete(boolean success);
    }

    /**
     * Drive backup info data class.
     */
    public static class DriveBackupInfo {
        public final String id;
        public final String name;
        public final long size;
        public final long createdTime;

        public DriveBackupInfo(String id, String name, long size, long createdTime) {
            this.id = id;
            this.name = name;
            this.size = size;
            this.createdTime = createdTime;
        }
    }
}
