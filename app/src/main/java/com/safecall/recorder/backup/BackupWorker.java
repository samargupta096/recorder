package com.safecall.recorder.backup;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.safecall.recorder.data.local.prefs.PreferencesManager;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

/**
 * WorkManager worker for scheduled Google Drive backups.
 */
@HiltWorker
public class BackupWorker extends Worker {

    public static final String WORK_NAME = "safecall_backup_work";

    private final DriveBackupManager driveBackupManager;
    private final PreferencesManager preferencesManager;
    private final GoogleSignInHelper googleSignInHelper;
    private final Context context;

    @AssistedInject
    public BackupWorker(
            @Assisted @NonNull Context context,
            @Assisted @NonNull WorkerParameters workerParams,
            DriveBackupManager driveBackupManager,
            PreferencesManager preferencesManager,
            GoogleSignInHelper googleSignInHelper
    ) {
        super(context, workerParams);
        this.context = context;
        this.driveBackupManager = driveBackupManager;
        this.preferencesManager = preferencesManager;
        this.googleSignInHelper = googleSignInHelper;
    }

    @NonNull
    @Override
    public Result doWork() {
        // Check if backup is still enabled
        if (!preferencesManager.isScheduledBackupEnabled()) {
            return Result.success();
        }

        // Check if signed in
        if (!googleSignInHelper.isSignedIn()) {
            return Result.failure();
        }

        // Check Wi-Fi requirement
        if (preferencesManager.isWifiOnlyBackup() && !isOnWifi()) {
            return Result.retry();
        }

        // Perform backup synchronously
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger backedUpCount = new AtomicInteger(0);

        driveBackupManager.backupAllUnbacked(count -> {
            backedUpCount.set(count);
            latch.countDown();
        });

        try {
            latch.await(5, TimeUnit.MINUTES);
            return Result.success();
        } catch (InterruptedException e) {
            return Result.retry();
        }
    }

    /**
     * Check if device is connected to Wi-Fi.
     */
    private boolean isOnWifi() {
        ConnectivityManager connectivityManager = 
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        if (network == null) return false;
        
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
        return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
    }

    /**
     * Schedule periodic backup.
     */
    public static void schedulePeriodicBackup(Context context, boolean wifiOnly, long intervalHours) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(wifiOnly ? NetworkType.UNMETERED : NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build();

        PeriodicWorkRequest backupRequest = new PeriodicWorkRequest.Builder(
                BackupWorker.class,
                intervalHours,
                TimeUnit.HOURS
        )
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                        WORK_NAME,
                        ExistingPeriodicWorkPolicy.UPDATE,
                        backupRequest
                );
    }

    /**
     * Cancel scheduled backup.
     */
    public static void cancelPeriodicBackup(Context context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME);
    }

    /**
     * Run backup immediately (one-time).
     */
    public static void runImmediateBackup(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest backupRequest = new OneTimeWorkRequest.Builder(BackupWorker.class)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context).enqueue(backupRequest);
    }
}
