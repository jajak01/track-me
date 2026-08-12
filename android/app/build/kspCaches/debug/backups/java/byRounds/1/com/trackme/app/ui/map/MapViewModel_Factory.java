package com.trackme.app.ui.map;

import android.app.Application;
import com.trackme.app.data.api.WebSocketManager;
import com.trackme.app.data.location.LocationClient;
import com.trackme.app.data.repository.FriendRepository;
import com.trackme.app.data.repository.LocationRepository;
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
public final class MapViewModel_Factory implements Factory<MapViewModel> {
  private final Provider<Application> appProvider;

  private final Provider<FriendRepository> friendRepositoryProvider;

  private final Provider<LocationRepository> locationRepositoryProvider;

  private final Provider<WebSocketManager> wsManagerProvider;

  private final Provider<LocationClient> locationClientProvider;

  public MapViewModel_Factory(Provider<Application> appProvider,
      Provider<FriendRepository> friendRepositoryProvider,
      Provider<LocationRepository> locationRepositoryProvider,
      Provider<WebSocketManager> wsManagerProvider,
      Provider<LocationClient> locationClientProvider) {
    this.appProvider = appProvider;
    this.friendRepositoryProvider = friendRepositoryProvider;
    this.locationRepositoryProvider = locationRepositoryProvider;
    this.wsManagerProvider = wsManagerProvider;
    this.locationClientProvider = locationClientProvider;
  }

  @Override
  public MapViewModel get() {
    return newInstance(appProvider.get(), friendRepositoryProvider.get(), locationRepositoryProvider.get(), wsManagerProvider.get(), locationClientProvider.get());
  }

  public static MapViewModel_Factory create(Provider<Application> appProvider,
      Provider<FriendRepository> friendRepositoryProvider,
      Provider<LocationRepository> locationRepositoryProvider,
      Provider<WebSocketManager> wsManagerProvider,
      Provider<LocationClient> locationClientProvider) {
    return new MapViewModel_Factory(appProvider, friendRepositoryProvider, locationRepositoryProvider, wsManagerProvider, locationClientProvider);
  }

  public static MapViewModel newInstance(Application app, FriendRepository friendRepository,
      LocationRepository locationRepository, WebSocketManager wsManager,
      LocationClient locationClient) {
    return new MapViewModel(app, friendRepository, locationRepository, wsManager, locationClient);
  }
}
