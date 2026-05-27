package com.safecall.recorder.service;

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
public final class CallRecorderService_MembersInjector implements MembersInjector<CallRecorderService> {
  private final Provider<RecordingRepository> repositoryProvider;

  private final Provider<EncryptionManager> encryptionManagerProvider;

  private final Provider<PreferencesManager> preferencesManagerProvider;

  public CallRecorderService_MembersInjector(Provider<RecordingRepository> repositoryProvider,
      Provider<EncryptionManager> encryptionManagerProvider,
      Provider<PreferencesManager> preferencesManagerProvider) {
    this.repositoryProvider = repositoryProvider;
    this.encryptionManagerProvider = encryptionManagerProvider;
    this.preferencesManagerProvider = preferencesManagerProvider;
  }

  public static MembersInjector<CallRecorderService> create(
      Provider<RecordingRepository> repositoryProvider,
      Provider<EncryptionManager> encryptionManagerProvider,
      Provider<PreferencesManager> preferencesManagerProvider) {
    return new CallRecorderService_MembersInjector(repositoryProvider, encryptionManagerProvider, preferencesManagerProvider);
  }

  @Override
  public void injectMembers(CallRecorderService instance) {
    injectRepository(instance, repositoryProvider.get());
    injectEncryptionManager(instance, encryptionManagerProvider.get());
    injectPreferencesManager(instance, preferencesManagerProvider.get());
  }

  @InjectedFieldSignature("com.safecall.recorder.service.CallRecorderService.repository")
  public static void injectRepository(CallRecorderService instance,
      RecordingRepository repository) {
    instance.repository = repository;
  }

  @InjectedFieldSignature("com.safecall.recorder.service.CallRecorderService.encryptionManager")
  public static void injectEncryptionManager(CallRecorderService instance,
      EncryptionManager encryptionManager) {
    instance.encryptionManager = encryptionManager;
  }

  @InjectedFieldSignature("com.safecall.recorder.service.CallRecorderService.preferencesManager")
  public static void injectPreferencesManager(CallRecorderService instance,
      PreferencesManager preferencesManager) {
    instance.preferencesManager = preferencesManager;
  }
}
