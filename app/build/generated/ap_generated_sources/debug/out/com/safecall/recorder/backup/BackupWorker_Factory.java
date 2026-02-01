package com.safecall.recorder.backup;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.safecall.recorder.data.local.prefs.PreferencesManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class BackupWorker_Factory {
  private final Provider<DriveBackupManager> driveBackupManagerProvider;

  private final Provider<PreferencesManager> preferencesManagerProvider;

  private final Provider<GoogleSignInHelper> googleSignInHelperProvider;

  public BackupWorker_Factory(Provider<DriveBackupManager> driveBackupManagerProvider,
      Provider<PreferencesManager> preferencesManagerProvider,
      Provider<GoogleSignInHelper> googleSignInHelperProvider) {
    this.driveBackupManagerProvider = driveBackupManagerProvider;
    this.preferencesManagerProvider = preferencesManagerProvider;
    this.googleSignInHelperProvider = googleSignInHelperProvider;
  }

  public BackupWorker get(Context context, WorkerParameters workerParams) {
    return newInstance(context, workerParams, driveBackupManagerProvider.get(), preferencesManagerProvider.get(), googleSignInHelperProvider.get());
  }

  public static BackupWorker_Factory create(Provider<DriveBackupManager> driveBackupManagerProvider,
      Provider<PreferencesManager> preferencesManagerProvider,
      Provider<GoogleSignInHelper> googleSignInHelperProvider) {
    return new BackupWorker_Factory(driveBackupManagerProvider, preferencesManagerProvider, googleSignInHelperProvider);
  }

  public static BackupWorker newInstance(Context context, WorkerParameters workerParams,
      DriveBackupManager driveBackupManager, PreferencesManager preferencesManager,
      GoogleSignInHelper googleSignInHelper) {
    return new BackupWorker(context, workerParams, driveBackupManager, preferencesManager, googleSignInHelper);
  }
}
