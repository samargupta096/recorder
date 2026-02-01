package com.safecall.recorder.di;

import com.safecall.recorder.data.local.db.AppDatabase;
import com.safecall.recorder.data.local.db.RecordingDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvideRecordingDaoFactory implements Factory<RecordingDao> {
  private final AppModule module;

  private final Provider<AppDatabase> databaseProvider;

  public AppModule_ProvideRecordingDaoFactory(AppModule module,
      Provider<AppDatabase> databaseProvider) {
    this.module = module;
    this.databaseProvider = databaseProvider;
  }

  @Override
  public RecordingDao get() {
    return provideRecordingDao(module, databaseProvider.get());
  }

  public static AppModule_ProvideRecordingDaoFactory create(AppModule module,
      Provider<AppDatabase> databaseProvider) {
    return new AppModule_ProvideRecordingDaoFactory(module, databaseProvider);
  }

  public static RecordingDao provideRecordingDao(AppModule instance, AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(instance.provideRecordingDao(database));
  }
}
