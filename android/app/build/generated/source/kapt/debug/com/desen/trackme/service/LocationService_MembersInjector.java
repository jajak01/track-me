package com.desen.trackme.service;

import com.desen.trackme.data.PendingLocationStore;
import com.desen.trackme.domain.repository.LocationRepository;
import com.google.android.gms.location.FusedLocationProviderClient;
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
    "cast"
})
public final class LocationService_MembersInjector implements MembersInjector<LocationService> {
  private final Provider<LocationRepository> locationRepositoryProvider;

  private final Provider<PendingLocationStore> pendingStoreProvider;

  private final Provider<FusedLocationProviderClient> fusedLocationClientProvider;

  public LocationService_MembersInjector(Provider<LocationRepository> locationRepositoryProvider,
      Provider<PendingLocationStore> pendingStoreProvider,
      Provider<FusedLocationProviderClient> fusedLocationClientProvider) {
    this.locationRepositoryProvider = locationRepositoryProvider;
    this.pendingStoreProvider = pendingStoreProvider;
    this.fusedLocationClientProvider = fusedLocationClientProvider;
  }

  public static MembersInjector<LocationService> create(
      Provider<LocationRepository> locationRepositoryProvider,
      Provider<PendingLocationStore> pendingStoreProvider,
      Provider<FusedLocationProviderClient> fusedLocationClientProvider) {
    return new LocationService_MembersInjector(locationRepositoryProvider, pendingStoreProvider, fusedLocationClientProvider);
  }

  @Override
  public void injectMembers(LocationService instance) {
    injectLocationRepository(instance, locationRepositoryProvider.get());
    injectPendingStore(instance, pendingStoreProvider.get());
    injectFusedLocationClient(instance, fusedLocationClientProvider.get());
  }

  @InjectedFieldSignature("com.desen.trackme.service.LocationService.locationRepository")
  public static void injectLocationRepository(LocationService instance,
      LocationRepository locationRepository) {
    instance.locationRepository = locationRepository;
  }

  @InjectedFieldSignature("com.desen.trackme.service.LocationService.pendingStore")
  public static void injectPendingStore(LocationService instance,
      PendingLocationStore pendingStore) {
    instance.pendingStore = pendingStore;
  }

  @InjectedFieldSignature("com.desen.trackme.service.LocationService.fusedLocationClient")
  public static void injectFusedLocationClient(LocationService instance,
      FusedLocationProviderClient fusedLocationClient) {
    instance.fusedLocationClient = fusedLocationClient;
  }
}
