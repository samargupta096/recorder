package com.safecall.recorder.service;

import com.safecall.recorder.data.local.prefs.PreferencesManager;
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
public final class PhoneCallReceiver_MembersInjector implements MembersInjector<PhoneCallReceiver> {
  private final Provider<PreferencesManager> preferencesManagerProvider;

  public PhoneCallReceiver_MembersInjector(
      Provider<PreferencesManager> preferencesManagerProvider) {
    this.preferencesManagerProvider = preferencesManagerProvider;
  }

  public static MembersInjector<PhoneCallReceiver> create(
      Provider<PreferencesManager> preferencesManagerProvider) {
    return new PhoneCallReceiver_MembersInjector(preferencesManagerProvider);
  }

  @Override
  public void injectMembers(PhoneCallReceiver instance) {
    injectPreferencesManager(instance, preferencesManagerProvider.get());
  }

  @InjectedFieldSignature("com.safecall.recorder.service.PhoneCallReceiver.preferencesManager")
  public static void injectPreferencesManager(PhoneCallReceiver instance,
      PreferencesManager preferencesManager) {
    instance.preferencesManager = preferencesManager;
  }
}
