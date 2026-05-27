package com.safecall.recorder.data.local.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data Access Object for RecordingEntity.
 * Provides CRUD operations and search functionality.
 */
@Dao
public interface RecordingDao {

    /**
     * Get all active recordings ordered by timestamp (newest first).
     * Returns LiveData for reactive updates.
     */
    @Query("SELECT * FROM recordings WHERE isDeleted = 0 ORDER BY timestamp DESC")
    LiveData<List<RecordingEntity>> getAllRecordings();

    /**
     * Get a single recording by ID.
     */
    @Query("SELECT * FROM recordings WHERE id = :id")
    RecordingEntity getRecordingById(long id);

    /**
     * Get a single recording by ID as LiveData.
     */
    @Query("SELECT * FROM recordings WHERE id = :id")
    LiveData<RecordingEntity> getRecordingByIdLive(long id);

    /**
     * Search recordings by contact name, phone number, or custom name.
     */
    @Query("SELECT * FROM recordings " +
            "WHERE contactName LIKE '%' || :query || '%' " +
            "OR phoneNumber LIKE '%' || :query || '%' " +
            "OR customName LIKE '%' || :query || '%' " +
            "ORDER BY timestamp DESC")
    LiveData<List<RecordingEntity>> searchRecordings(String query);

    /**
     * Get all recordings that haven't been backed up yet.
     */
    @Query("SELECT * FROM recordings WHERE isBackedUp = 0 ORDER BY timestamp ASC")
    List<RecordingEntity> getUnbackedRecordings();

    /**
     * Insert a new recording.
     * 
     * @return The ID of the inserted recording.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertRecording(RecordingEntity recording);

    /**
     * Update an existing recording.
     */
    @Update
    void updateRecording(RecordingEntity recording);

    /**
     * Delete a recording.
     */
    @Delete
    void deleteRecording(RecordingEntity recording);

    /**
     * Delete a recording by ID.
     */
    @Query("DELETE FROM recordings WHERE id = :id")
    void deleteRecordingById(long id);

    /**
     * Mark a recording as backed up.
     */
    @Query("UPDATE recordings SET isBackedUp = 1, driveFileId = :driveFileId WHERE id = :id")
    void markAsBackedUp(long id, String driveFileId);

    /**
     * Update the custom name for a recording.
     */
    @Query("UPDATE recordings SET customName = :customName WHERE id = :id")
    void updateCustomName(long id, String customName);

    /**
     * Update favorite status for a recording.
     */
    @Query("UPDATE recordings SET isFavorite = :isFavorite WHERE id = :id")
    void updateFavoriteStatus(long id, boolean isFavorite);

    /**
     * Update notes for a recording.
     */
    @Query("UPDATE recordings SET notes = :notes WHERE id = :id")
    void updateNotes(long id, String notes);

    /**
     * Get total count of recordings.
     */
    @Query("SELECT COUNT(*) FROM recordings")
    int getRecordingCount();

    /**
     * Get total size of all recordings.
     */
    @Query("SELECT SUM(fileSize) FROM recordings")
    Long getTotalStorageUsed();

    /**
     * Get all deleted recordings (Recycle Bin).
     */
    @Query("SELECT * FROM recordings WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    LiveData<List<RecordingEntity>> getDeletedRecordings();

    /**
     * Move a recording to the recycle bin.
     */
    @Query("UPDATE recordings SET isDeleted = 1, deletedAt = :timestamp WHERE id = :id")
    void moveToRecycleBin(long id, long timestamp);

    /**
     * Restore a recording from the recycle bin.
     */
    @Query("UPDATE recordings SET isDeleted = 0, deletedAt = 0 WHERE id = :id")
    void restoreFromRecycleBin(long id);

    /**
     * Delete all recordings (for clear data feature).
     */
    @Query("DELETE FROM recordings")
    void deleteAllRecordings();
}
