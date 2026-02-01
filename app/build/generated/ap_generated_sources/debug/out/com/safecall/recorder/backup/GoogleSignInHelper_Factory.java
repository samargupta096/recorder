package com.safecall.recorder.backup;

import android.content.Context;
import com.safecall.recorder.data.local.prefs.PreferencesManager;
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
public final class GoogleSignInHelper_Factory implements Factory<GoogleSignInHelper> {
  private final Provider<Context> contextProvider;

  private final Provider<PreferencesManager> preferencesManagerProvider;

  public GoogleSignInHelper_Factory(Provider<Context> contextProvider,
      Provider<PreferencesManager> preferencesManagerProvider) {
    this.contextProvider = contextProvider;
    this.preferencesManagerProvider = preferencesManagerProvider;
  }

  @Override
  public GoogleSignInHelper get() {
    return newInstance(contextProvider.get(), preferencesManagerProvider.get());
  }

  public static GoogleSignInHelper_Factory create(Provider<Context> contextProvider,
      Provider<PreferencesManager> preferencesManagerProvider) {
    return new GoogleSignInHelper_Factory(contextProvider, preferencesManagerProvider);
  }

  public static GoogleSignInHelper newInstance(Context context,
      PreferencesManager preferencesManager) {
    return new GoogleSignInHelper(context, preferencesManager);
  }
}
