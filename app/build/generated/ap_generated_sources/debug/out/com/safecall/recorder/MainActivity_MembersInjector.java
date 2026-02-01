package com.safecall.recorder;

import com.safecall.recorder.backup.DriveBackupManager;
import com.safecall.recorder.backup.GoogleSignInHelper;
import com.safecall.recorder.data.local.prefs.PreferencesManager;
import com.safecall.recorder.data.repository.RecordingRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<PreferencesManager> preferencesManagerProvider;

  private final Provider<RecordingRepository> repositoryProvider;

  private final Provider<GoogleSignInHelper> googleSignInHelperProvider;

  private final Provider<DriveBackupManager> driveBackupManagerProvider;

  public MainActivity_MembersInjector(Provider<PreferencesManager> preferencesManagerProvider,
      Provider<RecordingRepository> repositoryProvider,
      Provider<GoogleSignInHelper> googleSignInHelperProvider,
      Provider<DriveBackupManager> driveBackupManagerProvider) {
    this.preferencesManagerProvider = preferencesManagerProvider;
    this.repositoryProvider = repositoryProvider;
    this.googleSignInHelperProvider = googleSignInHelperProvider;
    this.driveBackupManagerProvider = driveBackupManagerProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<PreferencesManager> preferencesManagerProvider,
      Provider<RecordingRepository> repositoryProvider,
      Provider<GoogleSignInHelper> googleSignInHelperProvider,
      Provider<DriveBackupManager> driveBackupManagerProvider) {
    return new MainActivity_MembersInjector(preferencesManagerProvider, repositoryProvider, googleSignInHelperProvider, driveBackupManagerProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectPreferencesManager(instance, preferencesManagerProvider.get());
    injectRepository(instance, repositoryProvider.get());
    injectGoogleSignInHelper(instance, googleSignInHelperProvider.get());
    injectDriveBackupManager(instance, driveBackupManagerProvider.get());
  }

  @InjectedFieldSignature("com.safecall.recorder.MainActivity.preferencesManager")
  public static void injectPreferencesManager(MainActivity instance,
      PreferencesManager preferencesManager) {
    instance.preferencesManager = preferencesManager;
  }

  @InjectedFieldSignature("com.safecall.recorder.MainActivity.repository")
  public static void injectRepository(MainActivity instance, RecordingRepository repository) {
    instance.repository = repository;
  }

  @InjectedFieldSignature("com.safecall.recorder.MainActivity.googleSignInHelper")
  public static void injectGoogleSignInHelper(MainActivity instance,
      GoogleSignInHelper googleSignInHelper) {
    instance.googleSignInHelper = googleSignInHelper;
  }

  @InjectedFieldSignature("com.safecall.recorder.MainActivity.driveBackupManager")
  public static void injectDriveBackupManager(MainActivity instance,
      DriveBackupManager driveBackupManager) {
    instance.driveBackupManager = driveBackupManager;
  }
}
