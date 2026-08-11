package com.trackme.app.di;

import com.trackme.app.data.api.NotificationApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

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
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class AppModule_ProvideNotificationApiFactory implements Factory<NotificationApi> {
  private final Provider<Retrofit> retrofitProvider;

  public AppModule_ProvideNotificationApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public NotificationApi get() {
    return provideNotificationApi(retrofitProvider.get());
  }

  public static AppModule_ProvideNotificationApiFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new AppModule_ProvideNotificationApiFactory(retrofitProvider);
  }

  public static NotificationApi provideNotificationApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideNotificationApi(retrofit));
  }
}
