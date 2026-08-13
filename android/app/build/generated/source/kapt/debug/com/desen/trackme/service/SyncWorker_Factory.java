package com.desen.trackme.service;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.desen.trackme.data.PendingLocationStore;
import com.desen.trackme.domain.repository.LocationRepository;
import dagger.internal.DaggerGenerated;
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
    "cast"
})
public final class SyncWorker_Factory {
  private final Provider<LocationRepository> locationRepositoryProvider;

  private final Provider<PendingLocationStore> pendingStoreProvider;

  public SyncWorker_Factory(Provider<LocationRepository> locationRepositoryProvider,
      Provider<PendingLocationStore> pendingStoreProvider) {
    this.locationRepositoryProvider = locationRepositoryProvider;
    this.pendingStoreProvider = pendingStoreProvider;
  }

  public SyncWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, locationRepositoryProvider.get(), pendingStoreProvider.get());
  }

  public static SyncWorker_Factory create(Provider<LocationRepository> locationRepositoryProvider,
      Provider<PendingLocationStore> pendingStoreProvider) {
    return new SyncWorker_Factory(locationRepositoryProvider, pendingStoreProvider);
  }

  public static SyncWorker newInstance(Context context, WorkerParameters params,
      LocationRepository locationRepository, PendingLocationStore pendingStore) {
    return new SyncWorker(context, params, locationRepository, pendingStore);
  }
}
