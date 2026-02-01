package com.safecall.recorder;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.work.Configuration;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;

/**
 * Application class for SafeCall Recorder.
 * Initializes Hilt dependency injection and notification channels.
 */
@HiltAndroidApp
public class SafeCallApp extends Application implements Configuration.Provider {

    public static final String RECORDING_CHANNEL_ID = "recording_channel";

    @Inject
    HiltWorkerFactory workerFactory;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    /**
     * Creates notification channels for Android O and above.
     * Uses low importance to minimize notification visibility during recording.
     */
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel recordingChannel = new NotificationChannel(
                    RECORDING_CHANNEL_ID,
                    getString(R.string.recording_channel_name),
                    NotificationManager.IMPORTANCE_LOW
            );
            recordingChannel.setDescription(getString(R.string.recording_channel_desc));
            recordingChannel.setShowBadge(false);
            recordingChannel.enableLights(false);
            recordingChannel.enableVibration(false);
            recordingChannel.setSound(null, null);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(recordingChannel);
        }
    }

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build();
    }
}
