package com.trackme.app.data.repository;

import com.trackme.app.data.api.NotificationApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class NotificationRepository_Factory implements Factory<NotificationRepository> {
  private final Provider<NotificationApi> apiProvider;

  public NotificationRepository_Factory(Provider<NotificationApi> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public NotificationRepository get() {
    return newInstance(apiProvider.get());
  }

  public static NotificationRepository_Factory create(Provider<NotificationApi> apiProvider) {
    return new NotificationRepository_Factory(apiProvider);
  }

  public static NotificationRepository newInstance(NotificationApi api) {
    return new NotificationRepository(api);
  }
}
