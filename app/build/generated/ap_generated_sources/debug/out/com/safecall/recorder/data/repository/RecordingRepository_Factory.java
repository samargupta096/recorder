package com.safecall.recorder.data.repository;

import android.content.Context;
import com.safecall.recorder.data.encryption.EncryptionManager;
import com.safecall.recorder.data.local.db.RecordingDao;
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
public final class RecordingRepository_Factory implements Factory<RecordingRepository> {
  private final Provider<RecordingDao> recordingDaoProvider;

  private final Provider<EncryptionManager> encryptionManagerProvider;

  private final Provider<Context> contextProvider;

  public RecordingRepository_Factory(Provider<RecordingDao> recordingDaoProvider,
      Provider<EncryptionManager> encryptionManagerProvider, Provider<Context> contextProvider) {
    this.recordingDaoProvider = recordingDaoProvider;
    this.encryptionManagerProvider = encryptionManagerProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public RecordingRepository get() {
    return newInstance(recordingDaoProvider.get(), encryptionManagerProvider.get(), contextProvider.get());
  }

  public static RecordingRepository_Factory create(Provider<RecordingDao> recordingDaoProvider,
      Provider<EncryptionManager> encryptionManagerProvider, Provider<Context> contextProvider) {
    return new RecordingRepository_Factory(recordingDaoProvider, encryptionManagerProvider, contextProvider);
  }

  public static RecordingRepository newInstance(RecordingDao recordingDao,
      EncryptionManager encryptionManager, Context context) {
    return new RecordingRepository(recordingDao, encryptionManager, context);
  }
}
