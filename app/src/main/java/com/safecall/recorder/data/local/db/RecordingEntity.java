package com.safecall.recorder.data.local.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room entity representing a call recording.
 * Contains all metadata about the recorded call.
 */
@Entity(tableName = "recordings")
public class RecordingEntity {
    @PrimaryKey(autoGenerate = true)
    private long id = 0;

    /** Path to the encrypted recording file */
    private String filePath;

    /** Contact name if available, null otherwise */
    private String contactName;

    /** Phone number of the call */
    private String phoneNumber;

    /** True if incoming call, false if outgoing */
    private boolean isIncoming;

    /** Timestamp when the recording started (Unix milliseconds) */
    private long timestamp;

    /** Duration of the recording in milliseconds */
    private long duration;

    /** Size of the recording file in bytes */
    private long fileSize;

    /** Whether the file is encrypted */
    private boolean isEncrypted = true;

    /** Whether the recording has been backed up to Google Drive */
    private boolean isBackedUp = false;

    /** Custom name given by user (for rename feature) */
    private String customName = null;

    /** Google Drive file ID if backed up */
    private String driveFileId = null;

    /** Whether the recording is marked as favorite */
    private boolean isFavorite = false;

    /** User notes for this recording */
    private String notes = null;

    /** Transcription text generated from audio */
    private String transcription = null;

    /** Category or tag for organization */
    private String tag = null;

    /** Recycle bin: whether the file is marked as deleted */
    private boolean isDeleted = false;

    /** Recycle bin: timestamp when the file was deleted */
    private long deletedAt = 0;

    public RecordingEntity(String filePath, String contactName, String phoneNumber, boolean isIncoming,
            long timestamp, long duration, long fileSize) {
        this.filePath = filePath;
        this.contactName = contactName;
        this.phoneNumber = phoneNumber;
        this.isIncoming = isIncoming;
        this.timestamp = timestamp;
        this.duration = duration;
        this.fileSize = fileSize;
    }

    // Default constructor for Room
    public RecordingEntity() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isIncoming() {
        return isIncoming;
    }

    public void setIncoming(boolean incoming) {
        isIncoming = incoming;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    public boolean isBackedUp() {
        return isBackedUp;
    }

    public void setBackedUp(boolean backedUp) {
        isBackedUp = backedUp;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public String getDriveFileId() {
        return driveFileId;
    }

    public void setDriveFileId(String driveFileId) {
        this.driveFileId = driveFileId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Get the display name for this recording.
     * Returns custom name if set, otherwise contact name if available, otherwise
     * phone number.
     */
    public String getDisplayName() {
        if (customName != null && !customName.isEmpty()) {
            return customName;
        } else if (contactName != null && !contactName.isEmpty()) {
            return contactName;
        } else {
            return phoneNumber != null ? phoneNumber : "Unknown";
        }
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public long getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(long deletedAt) {
        this.deletedAt = deletedAt;
    }
}
