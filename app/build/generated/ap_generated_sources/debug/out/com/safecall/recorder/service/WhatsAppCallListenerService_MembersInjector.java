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
public final class WhatsAppCallListenerService_MembersInjector implements MembersInjector<WhatsAppCallListenerService> {
  private final Provider<PreferencesManager> preferencesManagerProvider;

  public WhatsAppCallListenerService_MembersInjector(
      Provider<PreferencesManager> preferencesManagerProvider) {
    this.preferencesManagerProvider = preferencesManagerProvider;
  }

  public static MembersInjector<WhatsAppCallListenerService> create(
      Provider<PreferencesManager> preferencesManagerProvider) {
    return new WhatsAppCallListenerService_MembersInjector(preferencesManagerProvider);
  }

  @Override
  public void injectMembers(WhatsAppCallListenerService instance) {
    injectPreferencesManager(instance, preferencesManagerProvider.get());
  }

  @InjectedFieldSignature("com.safecall.recorder.service.WhatsAppCallListenerService.preferencesManager")
  public static void injectPreferencesManager(WhatsAppCallListenerService instance,
      PreferencesManager preferencesManager) {
    instance.preferencesManager = preferencesManager;
  }
}
