package com.safecall.recorder.data.local.db;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class RecordingDao_Impl implements RecordingDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RecordingEntity> __insertionAdapterOfRecordingEntity;

  private final EntityDeletionOrUpdateAdapter<RecordingEntity> __deletionAdapterOfRecordingEntity;

  private final EntityDeletionOrUpdateAdapter<RecordingEntity> __updateAdapterOfRecordingEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteRecordingById;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsBackedUp;

  private final SharedSQLiteStatement __preparedStmtOfUpdateCustomName;

  private final SharedSQLiteStatement __preparedStmtOfUpdateFavoriteStatus;

  private final SharedSQLiteStatement __preparedStmtOfUpdateNotes;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllRecordings;

  public RecordingDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRecordingEntity = new EntityInsertionAdapter<RecordingEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `recordings` (`id`,`filePath`,`contactName`,`phoneNumber`,`isIncoming`,`timestamp`,`duration`,`fileSize`,`isEncrypted`,`isBackedUp`,`customName`,`driveFileId`,`isFavorite`,`notes`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final RecordingEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getFilePath() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getFilePath());
        }
        if (entity.getContactName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getContactName());
        }
        if (entity.getPhoneNumber() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getPhoneNumber());
        }
        final int _tmp = entity.isIncoming() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getTimestamp());
        statement.bindLong(7, entity.getDuration());
        statement.bindLong(8, entity.getFileSize());
        final int _tmp_1 = entity.isEncrypted() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
        final int _tmp_2 = entity.isBackedUp() ? 1 : 0;
        statement.bindLong(10, _tmp_2);
        if (entity.getCustomName() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getCustomName());
        }
        if (entity.getDriveFileId() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getDriveFileId());
        }
        final int _tmp_3 = entity.isFavorite() ? 1 : 0;
        statement.bindLong(13, _tmp_3);
        if (entity.getNotes() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getNotes());
        }
      }
    };
    this.__deletionAdapterOfRecordingEntity = new EntityDeletionOrUpdateAdapter<RecordingEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `recordings` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final RecordingEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfRecordingEntity = new EntityDeletionOrUpdateAdapter<RecordingEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `recordings` SET `id` = ?,`filePath` = ?,`contactName` = ?,`phoneNumber` = ?,`isIncoming` = ?,`timestamp` = ?,`duration` = ?,`fileSize` = ?,`isEncrypted` = ?,`isBackedUp` = ?,`customName` = ?,`driveFileId` = ?,`isFavorite` = ?,`notes` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final RecordingEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getFilePath() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getFilePath());
        }
        if (entity.getContactName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getContactName());
        }
        if (entity.getPhoneNumber() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getPhoneNumber());
        }
        final int _tmp = entity.isIncoming() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getTimestamp());
        statement.bindLong(7, entity.getDuration());
        statement.bindLong(8, entity.getFileSize());
        final int _tmp_1 = entity.isEncrypted() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
        final int _tmp_2 = entity.isBackedUp() ? 1 : 0;
        statement.bindLong(10, _tmp_2);
        if (entity.getCustomName() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getCustomName());
        }
        if (entity.getDriveFileId() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getDriveFileId());
        }
        final int _tmp_3 = entity.isFavorite() ? 1 : 0;
        statement.bindLong(13, _tmp_3);
        if (entity.getNotes() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getNotes());
        }
        statement.bindLong(15, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteRecordingById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM recordings WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkAsBackedUp = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE recordings SET isBackedUp = 1, driveFileId = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateCustomName = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE recordings SET customName = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateFavoriteStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE recordings SET isFavorite = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateNotes = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE recordings SET notes = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllRecordings = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM recordings";
        return _query;
      }
    };
  }

  @Override
  public long insertRecording(final RecordingEntity recording) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfRecordingEntity.insertAndReturnId(recording);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteRecording(final RecordingEntity recording) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfRecordingEntity.handle(recording);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateRecording(final RecordingEntity recording) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfRecordingEntity.handle(recording);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteRecordingById(final long id) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteRecordingById.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, id);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteRecordingById.release(_stmt);
    }
  }

  @Override
  public void markAsBackedUp(final long id, final String driveFileId) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfMarkAsBackedUp.acquire();
    int _argIndex = 1;
    if (driveFileId == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, driveFileId);
    }
    _argIndex = 2;
    _stmt.bindLong(_argIndex, id);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfMarkAsBackedUp.release(_stmt);
    }
  }

  @Override
  public void updateCustomName(final long id, final String customName) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateCustomName.acquire();
    int _argIndex = 1;
    if (customName == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, customName);
    }
    _argIndex = 2;
    _stmt.bindLong(_argIndex, id);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfUpdateCustomName.release(_stmt);
    }
  }

  @Override
  public void updateFavoriteStatus(final long id, final boolean isFavorite) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateFavoriteStatus.acquire();
    int _argIndex = 1;
    final int _tmp = isFavorite ? 1 : 0;
    _stmt.bindLong(_argIndex, _tmp);
    _argIndex = 2;
    _stmt.bindLong(_argIndex, id);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfUpdateFavoriteStatus.release(_stmt);
    }
  }

  @Override
  public void updateNotes(final long id, final String notes) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateNotes.acquire();
    int _argIndex = 1;
    if (notes == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, notes);
    }
    _argIndex = 2;
    _stmt.bindLong(_argIndex, id);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfUpdateNotes.release(_stmt);
    }
  }

  @Override
  public void deleteAllRecordings() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllRecordings.acquire();
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteAllRecordings.release(_stmt);
    }
  }

  @Override
  public LiveData<List<RecordingEntity>> getAllRecordings() {
    final String _sql = "SELECT * FROM recordings ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"recordings"}, false, new Callable<List<RecordingEntity>>() {
      @Override
      @Nullable
      public List<RecordingEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfContactName = CursorUtil.getColumnIndexOrThrow(_cursor, "contactName");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfIsIncoming = CursorUtil.getColumnIndexOrThrow(_cursor, "isIncoming");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "fileSize");
          final int _cursorIndexOfIsEncrypted = CursorUtil.getColumnIndexOrThrow(_cursor, "isEncrypted");
          final int _cursorIndexOfIsBackedUp = CursorUtil.getColumnIndexOrThrow(_cursor, "isBackedUp");
          final int _cursorIndexOfCustomName = CursorUtil.getColumnIndexOrThrow(_cursor, "customName");
          final int _cursorIndexOfDriveFileId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveFileId");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<RecordingEntity> _result = new ArrayList<RecordingEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RecordingEntity _item;
            _item = new RecordingEntity();
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            _item.setId(_tmpId);
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            _item.setFilePath(_tmpFilePath);
            final String _tmpContactName;
            if (_cursor.isNull(_cursorIndexOfContactName)) {
              _tmpContactName = null;
            } else {
              _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            }
            _item.setContactName(_tmpContactName);
            final String _tmpPhoneNumber;
            if (_cursor.isNull(_cursorIndexOfPhoneNumber)) {
              _tmpPhoneNumber = null;
            } else {
              _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            }
            _item.setPhoneNumber(_tmpPhoneNumber);
            final boolean _tmpIsIncoming;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsIncoming);
            _tmpIsIncoming = _tmp != 0;
            _item.setIncoming(_tmpIsIncoming);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item.setTimestamp(_tmpTimestamp);
            final long _tmpDuration;
            _tmpDuration = _cursor.getLong(_cursorIndexOfDuration);
            _item.setDuration(_tmpDuration);
            final long _tmpFileSize;
            _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            _item.setFileSize(_tmpFileSize);
            final boolean _tmpIsEncrypted;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsEncrypted);
            _tmpIsEncrypted = _tmp_1 != 0;
            _item.setEncrypted(_tmpIsEncrypted);
            final boolean _tmpIsBackedUp;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsBackedUp);
            _tmpIsBackedUp = _tmp_2 != 0;
            _item.setBackedUp(_tmpIsBackedUp);
            final String _tmpCustomName;
            if (_cursor.isNull(_cursorIndexOfCustomName)) {
              _tmpCustomName = null;
            } else {
              _tmpCustomName = _cursor.getString(_cursorIndexOfCustomName);
            }
            _item.setCustomName(_tmpCustomName);
            final String _tmpDriveFileId;
            if (_cursor.isNull(_cursorIndexOfDriveFileId)) {
              _tmpDriveFileId = null;
            } else {
              _tmpDriveFileId = _cursor.getString(_cursorIndexOfDriveFileId);
            }
            _item.setDriveFileId(_tmpDriveFileId);
            final boolean _tmpIsFavorite;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_3 != 0;
            _item.setFavorite(_tmpIsFavorite);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item.setNotes(_tmpNotes);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public RecordingEntity getRecordingById(final long id) {
    final String _sql = "SELECT * FROM recordings WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
      final int _cursorIndexOfContactName = CursorUtil.getColumnIndexOrThrow(_cursor, "contactName");
      final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
      final int _cursorIndexOfIsIncoming = CursorUtil.getColumnIndexOrThrow(_cursor, "isIncoming");
      final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
      final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
      final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "fileSize");
      final int _cursorIndexOfIsEncrypted = CursorUtil.getColumnIndexOrThrow(_cursor, "isEncrypted");
      final int _cursorIndexOfIsBackedUp = CursorUtil.getColumnIndexOrThrow(_cursor, "isBackedUp");
      final int _cursorIndexOfCustomName = CursorUtil.getColumnIndexOrThrow(_cursor, "customName");
      final int _cursorIndexOfDriveFileId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveFileId");
      final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
      final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
      final RecordingEntity _result;
      if (_cursor.moveToFirst()) {
        _result = new RecordingEntity();
        final long _tmpId;
        _tmpId = _cursor.getLong(_cursorIndexOfId);
        _result.setId(_tmpId);
        final String _tmpFilePath;
        if (_cursor.isNull(_cursorIndexOfFilePath)) {
          _tmpFilePath = null;
        } else {
          _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
        }
        _result.setFilePath(_tmpFilePath);
        final String _tmpContactName;
        if (_cursor.isNull(_cursorIndexOfContactName)) {
          _tmpContactName = null;
        } else {
          _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
        }
        _result.setContactName(_tmpContactName);
        final String _tmpPhoneNumber;
        if (_cursor.isNull(_cursorIndexOfPhoneNumber)) {
          _tmpPhoneNumber = null;
        } else {
          _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
        }
        _result.setPhoneNumber(_tmpPhoneNumber);
        final boolean _tmpIsIncoming;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsIncoming);
        _tmpIsIncoming = _tmp != 0;
        _result.setIncoming(_tmpIsIncoming);
        final long _tmpTimestamp;
        _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
        _result.setTimestamp(_tmpTimestamp);
        final long _tmpDuration;
        _tmpDuration = _cursor.getLong(_cursorIndexOfDuration);
        _result.setDuration(_tmpDuration);
        final long _tmpFileSize;
        _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
        _result.setFileSize(_tmpFileSize);
        final boolean _tmpIsEncrypted;
        final int _tmp_1;
        _tmp_1 = _cursor.getInt(_cursorIndexOfIsEncrypted);
        _tmpIsEncrypted = _tmp_1 != 0;
        _result.setEncrypted(_tmpIsEncrypted);
        final boolean _tmpIsBackedUp;
        final int _tmp_2;
        _tmp_2 = _cursor.getInt(_cursorIndexOfIsBackedUp);
        _tmpIsBackedUp = _tmp_2 != 0;
        _result.setBackedUp(_tmpIsBackedUp);
        final String _tmpCustomName;
        if (_cursor.isNull(_cursorIndexOfCustomName)) {
          _tmpCustomName = null;
        } else {
          _tmpCustomName = _cursor.getString(_cursorIndexOfCustomName);
        }
        _result.setCustomName(_tmpCustomName);
        final String _tmpDriveFileId;
        if (_cursor.isNull(_cursorIndexOfDriveFileId)) {
          _tmpDriveFileId = null;
        } else {
          _tmpDriveFileId = _cursor.getString(_cursorIndexOfDriveFileId);
        }
        _result.setDriveFileId(_tmpDriveFileId);
        final boolean _tmpIsFavorite;
        final int _tmp_3;
        _tmp_3 = _cursor.getInt(_cursorIndexOfIsFavorite);
        _tmpIsFavorite = _tmp_3 != 0;
        _result.setFavorite(_tmpIsFavorite);
        final String _tmpNotes;
        if (_cursor.isNull(_cursorIndexOfNotes)) {
          _tmpNotes = null;
        } else {
          _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
        }
        _result.setNotes(_tmpNotes);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public LiveData<RecordingEntity> getRecordingByIdLive(final long id) {
    final String _sql = "SELECT * FROM recordings WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return __db.getInvalidationTracker().createLiveData(new String[] {"recordings"}, false, new Callable<RecordingEntity>() {
      @Override
      @Nullable
      public RecordingEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfContactName = CursorUtil.getColumnIndexOrThrow(_cursor, "contactName");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfIsIncoming = CursorUtil.getColumnIndexOrThrow(_cursor, "isIncoming");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "fileSize");
          final int _cursorIndexOfIsEncrypted = CursorUtil.getColumnIndexOrThrow(_cursor, "isEncrypted");
          final int _cursorIndexOfIsBackedUp = CursorUtil.getColumnIndexOrThrow(_cursor, "isBackedUp");
          final int _cursorIndexOfCustomName = CursorUtil.getColumnIndexOrThrow(_cursor, "customName");
          final int _cursorIndexOfDriveFileId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveFileId");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final RecordingEntity _result;
          if (_cursor.moveToFirst()) {
            _result = new RecordingEntity();
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            _result.setId(_tmpId);
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            _result.setFilePath(_tmpFilePath);
            final String _tmpContactName;
            if (_cursor.isNull(_cursorIndexOfContactName)) {
              _tmpContactName = null;
            } else {
              _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            }
            _result.setContactName(_tmpContactName);
            final String _tmpPhoneNumber;
            if (_cursor.isNull(_cursorIndexOfPhoneNumber)) {
              _tmpPhoneNumber = null;
            } else {
              _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            }
            _result.setPhoneNumber(_tmpPhoneNumber);
            final boolean _tmpIsIncoming;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsIncoming);
            _tmpIsIncoming = _tmp != 0;
            _result.setIncoming(_tmpIsIncoming);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _result.setTimestamp(_tmpTimestamp);
            final long _tmpDuration;
            _tmpDuration = _cursor.getLong(_cursorIndexOfDuration);
            _result.setDuration(_tmpDuration);
            final long _tmpFileSize;
            _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            _result.setFileSize(_tmpFileSize);
            final boolean _tmpIsEncrypted;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsEncrypted);
            _tmpIsEncrypted = _tmp_1 != 0;
            _result.setEncrypted(_tmpIsEncrypted);
            final boolean _tmpIsBackedUp;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsBackedUp);
            _tmpIsBackedUp = _tmp_2 != 0;
            _result.setBackedUp(_tmpIsBackedUp);
            final String _tmpCustomName;
            if (_cursor.isNull(_cursorIndexOfCustomName)) {
              _tmpCustomName = null;
            } else {
              _tmpCustomName = _cursor.getString(_cursorIndexOfCustomName);
            }
            _result.setCustomName(_tmpCustomName);
            final String _tmpDriveFileId;
            if (_cursor.isNull(_cursorIndexOfDriveFileId)) {
              _tmpDriveFileId = null;
            } else {
              _tmpDriveFileId = _cursor.getString(_cursorIndexOfDriveFileId);
            }
            _result.setDriveFileId(_tmpDriveFileId);
            final boolean _tmpIsFavorite;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_3 != 0;
            _result.setFavorite(_tmpIsFavorite);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _result.setNotes(_tmpNotes);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<List<RecordingEntity>> searchRecordings(final String query) {
    final String _sql = "SELECT * FROM recordings WHERE contactName LIKE '%' || ? || '%' OR phoneNumber LIKE '%' || ? || '%' OR customName LIKE '%' || ? || '%' ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
    }
    _argIndex = 2;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
    }
    _argIndex = 3;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"recordings"}, false, new Callable<List<RecordingEntity>>() {
      @Override
      @Nullable
      public List<RecordingEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfContactName = CursorUtil.getColumnIndexOrThrow(_cursor, "contactName");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfIsIncoming = CursorUtil.getColumnIndexOrThrow(_cursor, "isIncoming");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "fileSize");
          final int _cursorIndexOfIsEncrypted = CursorUtil.getColumnIndexOrThrow(_cursor, "isEncrypted");
          final int _cursorIndexOfIsBackedUp = CursorUtil.getColumnIndexOrThrow(_cursor, "isBackedUp");
          final int _cursorIndexOfCustomName = CursorUtil.getColumnIndexOrThrow(_cursor, "customName");
          final int _cursorIndexOfDriveFileId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveFileId");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<RecordingEntity> _result = new ArrayList<RecordingEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RecordingEntity _item;
            _item = new RecordingEntity();
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            _item.setId(_tmpId);
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            _item.setFilePath(_tmpFilePath);
            final String _tmpContactName;
            if (_cursor.isNull(_cursorIndexOfContactName)) {
              _tmpContactName = null;
            } else {
              _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            }
            _item.setContactName(_tmpContactName);
            final String _tmpPhoneNumber;
            if (_cursor.isNull(_cursorIndexOfPhoneNumber)) {
              _tmpPhoneNumber = null;
            } else {
              _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            }
            _item.setPhoneNumber(_tmpPhoneNumber);
            final boolean _tmpIsIncoming;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsIncoming);
            _tmpIsIncoming = _tmp != 0;
            _item.setIncoming(_tmpIsIncoming);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item.setTimestamp(_tmpTimestamp);
            final long _tmpDuration;
            _tmpDuration = _cursor.getLong(_cursorIndexOfDuration);
            _item.setDuration(_tmpDuration);
            final long _tmpFileSize;
            _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            _item.setFileSize(_tmpFileSize);
            final boolean _tmpIsEncrypted;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsEncrypted);
            _tmpIsEncrypted = _tmp_1 != 0;
            _item.setEncrypted(_tmpIsEncrypted);
            final boolean _tmpIsBackedUp;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsBackedUp);
            _tmpIsBackedUp = _tmp_2 != 0;
            _item.setBackedUp(_tmpIsBackedUp);
            final String _tmpCustomName;
            if (_cursor.isNull(_cursorIndexOfCustomName)) {
              _tmpCustomName = null;
            } else {
              _tmpCustomName = _cursor.getString(_cursorIndexOfCustomName);
            }
            _item.setCustomName(_tmpCustomName);
            final String _tmpDriveFileId;
            if (_cursor.isNull(_cursorIndexOfDriveFileId)) {
              _tmpDriveFileId = null;
            } else {
              _tmpDriveFileId = _cursor.getString(_cursorIndexOfDriveFileId);
            }
            _item.setDriveFileId(_tmpDriveFileId);
            final boolean _tmpIsFavorite;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_3 != 0;
            _item.setFavorite(_tmpIsFavorite);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item.setNotes(_tmpNotes);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public List<RecordingEntity> getUnbackedRecordings() {
    final String _sql = "SELECT * FROM recordings WHERE isBackedUp = 0 ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
      final int _cursorIndexOfContactName = CursorUtil.getColumnIndexOrThrow(_cursor, "contactName");
      final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
      final int _cursorIndexOfIsIncoming = CursorUtil.getColumnIndexOrThrow(_cursor, "isIncoming");
      final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
      final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
      final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "fileSize");
      final int _cursorIndexOfIsEncrypted = CursorUtil.getColumnIndexOrThrow(_cursor, "isEncrypted");
      final int _cursorIndexOfIsBackedUp = CursorUtil.getColumnIndexOrThrow(_cursor, "isBackedUp");
      final int _cursorIndexOfCustomName = CursorUtil.getColumnIndexOrThrow(_cursor, "customName");
      final int _cursorIndexOfDriveFileId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveFileId");
      final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
      final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
      final List<RecordingEntity> _result = new ArrayList<RecordingEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final RecordingEntity _item;
        _item = new RecordingEntity();
        final long _tmpId;
        _tmpId = _cursor.getLong(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpFilePath;
        if (_cursor.isNull(_cursorIndexOfFilePath)) {
          _tmpFilePath = null;
        } else {
          _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
        }
        _item.setFilePath(_tmpFilePath);
        final String _tmpContactName;
        if (_cursor.isNull(_cursorIndexOfContactName)) {
          _tmpContactName = null;
        } else {
          _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
        }
        _item.setContactName(_tmpContactName);
        final String _tmpPhoneNumber;
        if (_cursor.isNull(_cursorIndexOfPhoneNumber)) {
          _tmpPhoneNumber = null;
        } else {
          _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
        }
        _item.setPhoneNumber(_tmpPhoneNumber);
        final boolean _tmpIsIncoming;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsIncoming);
        _tmpIsIncoming = _tmp != 0;
        _item.setIncoming(_tmpIsIncoming);
        final long _tmpTimestamp;
        _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
        _item.setTimestamp(_tmpTimestamp);
        final long _tmpDuration;
        _tmpDuration = _cursor.getLong(_cursorIndexOfDuration);
        _item.setDuration(_tmpDuration);
        final long _tmpFileSize;
        _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
        _item.setFileSize(_tmpFileSize);
        final boolean _tmpIsEncrypted;
        final int _tmp_1;
        _tmp_1 = _cursor.getInt(_cursorIndexOfIsEncrypted);
        _tmpIsEncrypted = _tmp_1 != 0;
        _item.setEncrypted(_tmpIsEncrypted);
        final boolean _tmpIsBackedUp;
        final int _tmp_2;
        _tmp_2 = _cursor.getInt(_cursorIndexOfIsBackedUp);
        _tmpIsBackedUp = _tmp_2 != 0;
        _item.setBackedUp(_tmpIsBackedUp);
        final String _tmpCustomName;
        if (_cursor.isNull(_cursorIndexOfCustomName)) {
          _tmpCustomName = null;
        } else {
          _tmpCustomName = _cursor.getString(_cursorIndexOfCustomName);
        }
        _item.setCustomName(_tmpCustomName);
        final String _tmpDriveFileId;
        if (_cursor.isNull(_cursorIndexOfDriveFileId)) {
          _tmpDriveFileId = null;
        } else {
          _tmpDriveFileId = _cursor.getString(_cursorIndexOfDriveFileId);
        }
        _item.setDriveFileId(_tmpDriveFileId);
        final boolean _tmpIsFavorite;
        final int _tmp_3;
        _tmp_3 = _cursor.getInt(_cursorIndexOfIsFavorite);
        _tmpIsFavorite = _tmp_3 != 0;
        _item.setFavorite(_tmpIsFavorite);
        final String _tmpNotes;
        if (_cursor.isNull(_cursorIndexOfNotes)) {
          _tmpNotes = null;
        } else {
          _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
        }
        _item.setNotes(_tmpNotes);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public int getRecordingCount() {
    final String _sql = "SELECT COUNT(*) FROM recordings";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _result;
      if (_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Long getTotalStorageUsed() {
    final String _sql = "SELECT SUM(fileSize) FROM recordings";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final Long _result;
      if (_cursor.moveToFirst()) {
        final Long _tmp;
        if (_cursor.isNull(0)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getLong(0);
        }
        _result = _tmp;
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
