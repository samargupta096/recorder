package com.safecall.recorder.di;

import android.content.Context;

import com.safecall.recorder.data.local.db.AppDatabase;
import com.safecall.recorder.data.local.db.RecordingDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

/**
 * Hilt module providing app-wide dependencies.
 */
@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    /**
     * Provides the Room database instance.
     */
    @Provides
    @Singleton
    public AppDatabase provideDatabase(@ApplicationContext Context context) {
        return AppDatabase.getInstance(context);
    }

    /**
     * Provides the RecordingDao from the database.
     */
    @Provides
    @Singleton
    public RecordingDao provideRecordingDao(AppDatabase database) {
        return database.recordingDao();
    }
}
