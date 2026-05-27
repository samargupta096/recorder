package com.safecall.recorder.data.local.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Room database for SafeCall Recorder.
 * Stores all recording metadata.
 */
@Database(
    entities = {RecordingEntity.class},
    version = 2,
    exportSchema = true
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract RecordingDao recordingDao();

    private static final String DATABASE_NAME = "safecall_recorder.db";
    private static volatile AppDatabase INSTANCE = null;

    /**
     * Get the singleton database instance.
     */
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = buildDatabase(context);
                }
            }
        }
        return INSTANCE;
    }

    private static AppDatabase buildDatabase(Context context) {
        return Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                DATABASE_NAME
        )
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build();
    }

    static final androidx.room.migration.Migration MIGRATION_1_2 = new androidx.room.migration.Migration(1, 2) {
        @Override
        public void migrate(androidx.sqlite.db.SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE recordings ADD COLUMN transcription TEXT");
            database.execSQL("ALTER TABLE recordings ADD COLUMN tag TEXT");
            database.execSQL("ALTER TABLE recordings ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE recordings ADD COLUMN deletedAt INTEGER NOT NULL DEFAULT 0");
        }
    };
}
