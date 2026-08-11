package com.trackme.app.data.repository;

import com.trackme.app.data.api.LocationApi;
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
public final class LocationRepository_Factory implements Factory<LocationRepository> {
  private final Provider<LocationApi> apiProvider;

  public LocationRepository_Factory(Provider<LocationApi> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public LocationRepository get() {
    return newInstance(apiProvider.get());
  }

  public static LocationRepository_Factory create(Provider<LocationApi> apiProvider) {
    return new LocationRepository_Factory(apiProvider);
  }

  public static LocationRepository newInstance(LocationApi api) {
    return new LocationRepository(api);
  }
}
