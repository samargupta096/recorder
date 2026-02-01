package com.safecall.recorder.di;

import android.content.Context;
import com.safecall.recorder.data.local.db.AppDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideDatabaseFactory implements Factory<AppDatabase> {
  private final AppModule module;

  private final Provider<Context> contextProvider;

  public AppModule_ProvideDatabaseFactory(AppModule module, Provider<Context> contextProvider) {
    this.module = module;
    this.contextProvider = contextProvider;
  }

  @Override
  public AppDatabase get() {
    return provideDatabase(module, contextProvider.get());
  }

  public static AppModule_ProvideDatabaseFactory create(AppModule module,
      Provider<Context> contextProvider) {
    return new AppModule_ProvideDatabaseFactory(module, contextProvider);
  }

  public static AppDatabase provideDatabase(AppModule instance, Context context) {
    return Preconditions.checkNotNullFromProvides(instance.provideDatabase(context));
  }
}
