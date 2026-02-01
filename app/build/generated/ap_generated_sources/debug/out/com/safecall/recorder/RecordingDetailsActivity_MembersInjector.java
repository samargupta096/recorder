package com.safecall.recorder;

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
public final class RecordingDetailsActivity_MembersInjector implements MembersInjector<RecordingDetailsActivity> {
  private final Provider<RecordingRepository> repositoryProvider;

  public RecordingDetailsActivity_MembersInjector(
      Provider<RecordingRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  public static MembersInjector<RecordingDetailsActivity> create(
      Provider<RecordingRepository> repositoryProvider) {
    return new RecordingDetailsActivity_MembersInjector(repositoryProvider);
  }

  @Override
  public void injectMembers(RecordingDetailsActivity instance) {
    injectRepository(instance, repositoryProvider.get());
  }

  @InjectedFieldSignature("com.safecall.recorder.RecordingDetailsActivity.repository")
  public static void injectRepository(RecordingDetailsActivity instance,
      RecordingRepository repository) {
    instance.repository = repository;
  }
}
