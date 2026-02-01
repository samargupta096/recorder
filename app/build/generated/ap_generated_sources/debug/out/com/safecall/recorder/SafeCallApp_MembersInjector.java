package com.safecall.recorder;

import androidx.hilt.work.HiltWorkerFactory;
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
public final class SafeCallApp_MembersInjector implements MembersInjector<SafeCallApp> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public SafeCallApp_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<SafeCallApp> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new SafeCallApp_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(SafeCallApp instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.safecall.recorder.SafeCallApp.workerFactory")
  public static void injectWorkerFactory(SafeCallApp instance, HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
