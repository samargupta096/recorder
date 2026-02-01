package com.safecall.recorder;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.hilt.work.WorkerAssistedFactory;
import androidx.hilt.work.WorkerFactoryModule_ProvideFactoryFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.safecall.recorder.backup.BackupWorker;
import com.safecall.recorder.backup.BackupWorker_AssistedFactory;
import com.safecall.recorder.backup.DriveBackupManager;
import com.safecall.recorder.backup.GoogleSignInHelper;
import com.safecall.recorder.data.encryption.EncryptionManager;
import com.safecall.recorder.data.local.db.AppDatabase;
import com.safecall.recorder.data.local.db.RecordingDao;
import com.safecall.recorder.data.local.prefs.PreferencesManager;
import com.safecall.recorder.data.repository.RecordingRepository;
import com.safecall.recorder.di.AppModule;
import com.safecall.recorder.di.AppModule_ProvideDatabaseFactory;
import com.safecall.recorder.di.AppModule_ProvideRecordingDaoFactory;
import com.safecall.recorder.service.CallRecorderService;
import com.safecall.recorder.service.CallRecorderService_MembersInjector;
import com.safecall.recorder.service.PhoneCallReceiver;
import com.safecall.recorder.service.PhoneCallReceiver_MembersInjector;
import com.safecall.recorder.service.WhatsAppCallListenerService;
import com.safecall.recorder.service.WhatsAppCallListenerService_MembersInjector;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import dagger.internal.SingleCheck;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class DaggerSafeCallApp_HiltComponents_SingletonC {
  private DaggerSafeCallApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private AppModule appModule;

    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder appModule(AppModule appModule) {
      this.appModule = Preconditions.checkNotNull(appModule);
      return this;
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public SafeCallApp_HiltComponents.SingletonC build() {
      if (appModule == null) {
        this.appModule = new AppModule();
      }
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(appModule, applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements SafeCallApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public SafeCallApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements SafeCallApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public SafeCallApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements SafeCallApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public SafeCallApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements SafeCallApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public SafeCallApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements SafeCallApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public SafeCallApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements SafeCallApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public SafeCallApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements SafeCallApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public SafeCallApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends SafeCallApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends SafeCallApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends SafeCallApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends SafeCallApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @Override
    public void injectRecordingDetailsActivity(RecordingDetailsActivity recordingDetailsActivity) {
      injectRecordingDetailsActivity2(recordingDetailsActivity);
    }

    @Override
    public void injectSettingsActivity(SettingsActivity settingsActivity) {
      injectSettingsActivity2(settingsActivity);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(ImmutableMap.<Class<?>, Boolean>of(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return ImmutableMap.<Class<?>, Boolean>of();
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @CanIgnoreReturnValue
    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectPreferencesManager(instance, singletonCImpl.preferencesManagerProvider.get());
      MainActivity_MembersInjector.injectRepository(instance, singletonCImpl.recordingRepositoryProvider.get());
      MainActivity_MembersInjector.injectGoogleSignInHelper(instance, singletonCImpl.googleSignInHelperProvider.get());
      MainActivity_MembersInjector.injectDriveBackupManager(instance, singletonCImpl.driveBackupManagerProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private RecordingDetailsActivity injectRecordingDetailsActivity2(
        RecordingDetailsActivity instance) {
      RecordingDetailsActivity_MembersInjector.injectRepository(instance, singletonCImpl.recordingRepositoryProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private SettingsActivity injectSettingsActivity2(SettingsActivity instance) {
      SettingsActivity_MembersInjector.injectPreferencesManager(instance, singletonCImpl.preferencesManagerProvider.get());
      SettingsActivity_MembersInjector.injectGoogleSignInHelper(instance, singletonCImpl.googleSignInHelperProvider.get());
      SettingsActivity_MembersInjector.injectDriveBackupManager(instance, singletonCImpl.driveBackupManagerProvider.get());
      SettingsActivity_MembersInjector.injectRepository(instance, singletonCImpl.recordingRepositoryProvider.get());
      SettingsActivity_MembersInjector.injectEncryptionManager(instance, singletonCImpl.encryptionManagerProvider.get());
      return instance;
    }
  }

  private static final class ViewModelCImpl extends SafeCallApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public Map<Class<?>, Provider<ViewModel>> getHiltViewModelMap() {
      return ImmutableMap.<Class<?>, Provider<ViewModel>>of();
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }
  }

  private static final class ActivityRetainedCImpl extends SafeCallApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private dagger.internal.Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements dagger.internal.Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends SafeCallApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectCallRecorderService(CallRecorderService callRecorderService) {
      injectCallRecorderService2(callRecorderService);
    }

    @Override
    public void injectWhatsAppCallListenerService(
        WhatsAppCallListenerService whatsAppCallListenerService) {
      injectWhatsAppCallListenerService2(whatsAppCallListenerService);
    }

    @CanIgnoreReturnValue
    private CallRecorderService injectCallRecorderService2(CallRecorderService instance) {
      CallRecorderService_MembersInjector.injectRepository(instance, singletonCImpl.recordingRepositoryProvider.get());
      CallRecorderService_MembersInjector.injectEncryptionManager(instance, singletonCImpl.encryptionManagerProvider.get());
      CallRecorderService_MembersInjector.injectPreferencesManager(instance, singletonCImpl.preferencesManagerProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private WhatsAppCallListenerService injectWhatsAppCallListenerService2(
        WhatsAppCallListenerService instance) {
      WhatsAppCallListenerService_MembersInjector.injectPreferencesManager(instance, singletonCImpl.preferencesManagerProvider.get());
      return instance;
    }
  }

  private static final class SingletonCImpl extends SafeCallApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final AppModule appModule;

    private final SingletonCImpl singletonCImpl = this;

    private dagger.internal.Provider<PreferencesManager> preferencesManagerProvider;

    private dagger.internal.Provider<GoogleSignInHelper> googleSignInHelperProvider;

    private dagger.internal.Provider<AppDatabase> provideDatabaseProvider;

    private dagger.internal.Provider<RecordingDao> provideRecordingDaoProvider;

    private dagger.internal.Provider<EncryptionManager> encryptionManagerProvider;

    private dagger.internal.Provider<RecordingRepository> recordingRepositoryProvider;

    private dagger.internal.Provider<DriveBackupManager> driveBackupManagerProvider;

    private dagger.internal.Provider<BackupWorker_AssistedFactory> backupWorker_AssistedFactoryProvider;

    private SingletonCImpl(AppModule appModuleParam,
        ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      this.appModule = appModuleParam;
      initialize(appModuleParam, applicationContextModuleParam);

    }

    private Map<String, Provider<WorkerAssistedFactory<? extends ListenableWorker>>> mapOfStringAndProviderOfWorkerAssistedFactoryOf(
        ) {
      return ImmutableMap.<String, Provider<WorkerAssistedFactory<? extends ListenableWorker>>>of("com.safecall.recorder.backup.BackupWorker", ((dagger.internal.Provider) backupWorker_AssistedFactoryProvider));
    }

    private HiltWorkerFactory hiltWorkerFactory() {
      return WorkerFactoryModule_ProvideFactoryFactory.provideFactory(mapOfStringAndProviderOfWorkerAssistedFactoryOf());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final AppModule appModuleParam,
        final ApplicationContextModule applicationContextModuleParam) {
      this.preferencesManagerProvider = DoubleCheck.provider(new SwitchingProvider<PreferencesManager>(singletonCImpl, 3));
      this.googleSignInHelperProvider = DoubleCheck.provider(new SwitchingProvider<GoogleSignInHelper>(singletonCImpl, 2));
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 6));
      this.provideRecordingDaoProvider = DoubleCheck.provider(new SwitchingProvider<RecordingDao>(singletonCImpl, 5));
      this.encryptionManagerProvider = DoubleCheck.provider(new SwitchingProvider<EncryptionManager>(singletonCImpl, 7));
      this.recordingRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<RecordingRepository>(singletonCImpl, 4));
      this.driveBackupManagerProvider = DoubleCheck.provider(new SwitchingProvider<DriveBackupManager>(singletonCImpl, 1));
      this.backupWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<BackupWorker_AssistedFactory>(singletonCImpl, 0));
    }

    @Override
    public void injectSafeCallApp(SafeCallApp safeCallApp) {
      injectSafeCallApp2(safeCallApp);
    }

    @Override
    public void injectPhoneCallReceiver(PhoneCallReceiver phoneCallReceiver) {
      injectPhoneCallReceiver2(phoneCallReceiver);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    @CanIgnoreReturnValue
    private SafeCallApp injectSafeCallApp2(SafeCallApp instance) {
      SafeCallApp_MembersInjector.injectWorkerFactory(instance, hiltWorkerFactory());
      return instance;
    }

    @CanIgnoreReturnValue
    private PhoneCallReceiver injectPhoneCallReceiver2(PhoneCallReceiver instance) {
      PhoneCallReceiver_MembersInjector.injectPreferencesManager(instance, preferencesManagerProvider.get());
      return instance;
    }

    private static final class SwitchingProvider<T> implements dagger.internal.Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.safecall.recorder.backup.BackupWorker_AssistedFactory 
          return (T) new BackupWorker_AssistedFactory() {
            @Override
            public BackupWorker create(Context context, WorkerParameters workerParams) {
              return new BackupWorker(context, workerParams, singletonCImpl.driveBackupManagerProvider.get(), singletonCImpl.preferencesManagerProvider.get(), singletonCImpl.googleSignInHelperProvider.get());
            }
          };

          case 1: // com.safecall.recorder.backup.DriveBackupManager 
          return (T) new DriveBackupManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.googleSignInHelperProvider.get(), singletonCImpl.recordingRepositoryProvider.get(), singletonCImpl.encryptionManagerProvider.get(), singletonCImpl.preferencesManagerProvider.get());

          case 2: // com.safecall.recorder.backup.GoogleSignInHelper 
          return (T) new GoogleSignInHelper(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.preferencesManagerProvider.get());

          case 3: // com.safecall.recorder.data.local.prefs.PreferencesManager 
          return (T) new PreferencesManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 4: // com.safecall.recorder.data.repository.RecordingRepository 
          return (T) new RecordingRepository(singletonCImpl.provideRecordingDaoProvider.get(), singletonCImpl.encryptionManagerProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 5: // com.safecall.recorder.data.local.db.RecordingDao 
          return (T) AppModule_ProvideRecordingDaoFactory.provideRecordingDao(singletonCImpl.appModule, singletonCImpl.provideDatabaseProvider.get());

          case 6: // com.safecall.recorder.data.local.db.AppDatabase 
          return (T) AppModule_ProvideDatabaseFactory.provideDatabase(singletonCImpl.appModule, ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 7: // com.safecall.recorder.data.encryption.EncryptionManager 
          return (T) new EncryptionManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
