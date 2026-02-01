package com.safecall.recorder.backup;

import android.content.Context;
import com.safecall.recorder.data.encryption.EncryptionManager;
import com.safecall.recorder.data.local.prefs.PreferencesManager;
import com.safecall.recorder.data.repository.RecordingRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DriveBackupManager_Factory implements Factory<DriveBackupManager> {
  private final Provider<Context> contextProvider;

  private final Provider<GoogleSignInHelper> googleSignInHelperProvider;

  private final Provider<RecordingRepository> repositoryProvider;

  private final Provider<EncryptionManager> encryptionManagerProvider;

  private final Provider<PreferencesManager> preferencesManagerProvider;

  public DriveBackupManager_Factory(Provider<Context> contextProvider,
      Provider<GoogleSignInHelper> googleSignInHelperProvider,
      Provider<RecordingRepository> repositoryProvider,
      Provider<EncryptionManager> encryptionManagerProvider,
      Provider<PreferencesManager> preferencesManagerProvider) {
    this.contextProvider = contextProvider;
    this.googleSignInHelperProvider = googleSignInHelperProvider;
    this.repositoryProvider = repositoryProvider;
    this.encryptionManagerProvider = encryptionManagerProvider;
    this.preferencesManagerProvider = preferencesManagerProvider;
  }

  @Override
  public DriveBackupManager get() {
    return newInstance(contextProvider.get(), googleSignInHelperProvider.get(), repositoryProvider.get(), encryptionManagerProvider.get(), preferencesManagerProvider.get());
  }

  public static DriveBackupManager_Factory create(Provider<Context> contextProvider,
      Provider<GoogleSignInHelper> googleSignInHelperProvider,
      Provider<RecordingRepository> repositoryProvider,
      Provider<EncryptionManager> encryptionManagerProvider,
      Provider<PreferencesManager> preferencesManagerProvider) {
    return new DriveBackupManager_Factory(contextProvider, googleSignInHelperProvider, repositoryProvider, encryptionManagerProvider, preferencesManagerProvider);
  }

  public static DriveBackupManager newInstance(Context context,
      GoogleSignInHelper googleSignInHelper, RecordingRepository repository,
      EncryptionManager encryptionManager, PreferencesManager preferencesManager) {
    return new DriveBackupManager(context, googleSignInHelper, repository, encryptionManager, preferencesManager);
  }
}
