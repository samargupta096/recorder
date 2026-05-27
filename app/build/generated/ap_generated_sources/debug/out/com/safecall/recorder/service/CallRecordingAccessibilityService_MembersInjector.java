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
public final class CallRecordingAccessibilityService_MembersInjector implements MembersInjector<CallRecordingAccessibilityService> {
  private final Provider<PreferencesManager> preferencesManagerProvider;

  public CallRecordingAccessibilityService_MembersInjector(
      Provider<PreferencesManager> preferencesManagerProvider) {
    this.preferencesManagerProvider = preferencesManagerProvider;
  }

  public static MembersInjector<CallRecordingAccessibilityService> create(
      Provider<PreferencesManager> preferencesManagerProvider) {
    return new CallRecordingAccessibilityService_MembersInjector(preferencesManagerProvider);
  }

  @Override
  public void injectMembers(CallRecordingAccessibilityService instance) {
    injectPreferencesManager(instance, preferencesManagerProvider.get());
  }

  @InjectedFieldSignature("com.safecall.recorder.service.CallRecordingAccessibilityService.preferencesManager")
  public static void injectPreferencesManager(CallRecordingAccessibilityService instance,
      PreferencesManager preferencesManager) {
    instance.preferencesManager = preferencesManager;
  }
}
