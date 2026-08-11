package com.trackme.app.ui.notifications;

import com.trackme.app.data.repository.LocationRepository;
import com.trackme.app.data.repository.NotificationRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class NotificationsViewModel_Factory implements Factory<NotificationsViewModel> {
  private final Provider<NotificationRepository> notificationRepositoryProvider;

  private final Provider<LocationRepository> locationRepositoryProvider;

  public NotificationsViewModel_Factory(
      Provider<NotificationRepository> notificationRepositoryProvider,
      Provider<LocationRepository> locationRepositoryProvider) {
    this.notificationRepositoryProvider = notificationRepositoryProvider;
    this.locationRepositoryProvider = locationRepositoryProvider;
  }

  @Override
  public NotificationsViewModel get() {
    return newInstance(notificationRepositoryProvider.get(), locationRepositoryProvider.get());
  }

  public static NotificationsViewModel_Factory create(
      Provider<NotificationRepository> notificationRepositoryProvider,
      Provider<LocationRepository> locationRepositoryProvider) {
    return new NotificationsViewModel_Factory(notificationRepositoryProvider, locationRepositoryProvider);
  }

  public static NotificationsViewModel newInstance(NotificationRepository notificationRepository,
      LocationRepository locationRepository) {
    return new NotificationsViewModel(notificationRepository, locationRepository);
  }
}
