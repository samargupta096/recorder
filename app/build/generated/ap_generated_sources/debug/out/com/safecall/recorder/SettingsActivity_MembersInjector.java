package com.safecall.recorder;

import com.safecall.recorder.backup.DriveBackupManager;
import com.safecall.recorder.backup.GoogleSignInHelper;
import com.safecall.recorder.data.encryption.EncryptionManager;
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
public final class SettingsActivity_MembersInjector implements MembersInjector<SettingsActivity> {
  private final Provider<PreferencesManager> preferencesManagerProvider;

  private final Provider<GoogleSignInHelper> googleSignInHelperProvider;

  private final Provider<DriveBackupManager> driveBackupManagerProvider;

  private final Provider<RecordingRepository> repositoryProvider;

  private final Provider<EncryptionManager> encryptionManagerProvider;

  public SettingsActivity_MembersInjector(Provider<PreferencesManager> preferencesManagerProvider,
      Provider<GoogleSignInHelper> googleSignInHelperProvider,
      Provider<DriveBackupManager> driveBackupManagerProvider,
      Provider<RecordingRepository> repositoryProvider,
      Provider<EncryptionManager> encryptionManagerProvider) {
    this.preferencesManagerProvider = preferencesManagerProvider;
    this.googleSignInHelperProvider = googleSignInHelperProvider;
    this.driveBackupManagerProvider = driveBackupManagerProvider;
    this.repositoryProvider = repositoryProvider;
    this.encryptionManagerProvider = encryptionManagerProvider;
  }

  public static MembersInjector<SettingsActivity> create(
      Provider<PreferencesManager> preferencesManagerProvider,
      Provider<GoogleSignInHelper> googleSignInHelperProvider,
      Provider<DriveBackupManager> driveBackupManagerProvider,
      Provider<RecordingRepository> repositoryProvider,
      Provider<EncryptionManager> encryptionManagerProvider) {
    return new SettingsActivity_MembersInjector(preferencesManagerProvider, googleSignInHelperProvider, driveBackupManagerProvider, repositoryProvider, encryptionManagerProvider);
  }

  @Override
  public void injectMembers(SettingsActivity instance) {
    injectPreferencesManager(instance, preferencesManagerProvider.get());
    injectGoogleSignInHelper(instance, googleSignInHelperProvider.get());
    injectDriveBackupManager(instance, driveBackupManagerProvider.get());
    injectRepository(instance, repositoryProvider.get());
    injectEncryptionManager(instance, encryptionManagerProvider.get());
  }

  @InjectedFieldSignature("com.safecall.recorder.SettingsActivity.preferencesManager")
  public static void injectPreferencesManager(SettingsActivity instance,
      PreferencesManager preferencesManager) {
    instance.preferencesManager = preferencesManager;
  }

  @InjectedFieldSignature("com.safecall.recorder.SettingsActivity.googleSignInHelper")
  public static void injectGoogleSignInHelper(SettingsActivity instance,
      GoogleSignInHelper googleSignInHelper) {
    instance.googleSignInHelper = googleSignInHelper;
  }

  @InjectedFieldSignature("com.safecall.recorder.SettingsActivity.driveBackupManager")
  public static void injectDriveBackupManager(SettingsActivity instance,
      DriveBackupManager driveBackupManager) {
    instance.driveBackupManager = driveBackupManager;
  }

  @InjectedFieldSignature("com.safecall.recorder.SettingsActivity.repository")
  public static void injectRepository(SettingsActivity instance, RecordingRepository repository) {
    instance.repository = repository;
  }

  @InjectedFieldSignature("com.safecall.recorder.SettingsActivity.encryptionManager")
  public static void injectEncryptionManager(SettingsActivity instance,
      EncryptionManager encryptionManager) {
    instance.encryptionManager = encryptionManager;
  }
}
