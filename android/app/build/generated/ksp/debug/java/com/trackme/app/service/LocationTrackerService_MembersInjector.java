package com.trackme.app.service;

import com.trackme.app.data.api.WebSocketManager;
import com.trackme.app.data.local.TokenManager;
import com.trackme.app.data.location.LocationClient;
import com.trackme.app.data.repository.LocationRepository;
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
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class LocationTrackerService_MembersInjector implements MembersInjector<LocationTrackerService> {
  private final Provider<LocationClient> locationClientProvider;

  private final Provider<LocationRepository> locationRepositoryProvider;

  private final Provider<WebSocketManager> wsManagerProvider;

  private final Provider<TokenManager> tokenManagerProvider;

  public LocationTrackerService_MembersInjector(Provider<LocationClient> locationClientProvider,
      Provider<LocationRepository> locationRepositoryProvider,
      Provider<WebSocketManager> wsManagerProvider, Provider<TokenManager> tokenManagerProvider) {
    this.locationClientProvider = locationClientProvider;
    this.locationRepositoryProvider = locationRepositoryProvider;
    this.wsManagerProvider = wsManagerProvider;
    this.tokenManagerProvider = tokenManagerProvider;
  }

  public static MembersInjector<LocationTrackerService> create(
      Provider<LocationClient> locationClientProvider,
      Provider<LocationRepository> locationRepositoryProvider,
      Provider<WebSocketManager> wsManagerProvider, Provider<TokenManager> tokenManagerProvider) {
    return new LocationTrackerService_MembersInjector(locationClientProvider, locationRepositoryProvider, wsManagerProvider, tokenManagerProvider);
  }

  @Override
  public void injectMembers(LocationTrackerService instance) {
    injectLocationClient(instance, locationClientProvider.get());
    injectLocationRepository(instance, locationRepositoryProvider.get());
    injectWsManager(instance, wsManagerProvider.get());
    injectTokenManager(instance, tokenManagerProvider.get());
  }

  @InjectedFieldSignature("com.trackme.app.service.LocationTrackerService.locationClient")
  public static void injectLocationClient(LocationTrackerService instance,
      LocationClient locationClient) {
    instance.locationClient = locationClient;
  }

  @InjectedFieldSignature("com.trackme.app.service.LocationTrackerService.locationRepository")
  public static void injectLocationRepository(LocationTrackerService instance,
      LocationRepository locationRepository) {
    instance.locationRepository = locationRepository;
  }

  @InjectedFieldSignature("com.trackme.app.service.LocationTrackerService.wsManager")
  public static void injectWsManager(LocationTrackerService instance, WebSocketManager wsManager) {
    instance.wsManager = wsManager;
  }

  @InjectedFieldSignature("com.trackme.app.service.LocationTrackerService.tokenManager")
  public static void injectTokenManager(LocationTrackerService instance,
      TokenManager tokenManager) {
    instance.tokenManager = tokenManager;
  }
}
