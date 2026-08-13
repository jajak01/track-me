package com.desen.trackme.data.repository;

import com.desen.trackme.core.network.ApiService;
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
    "cast"
})
public final class LocationRepositoryImpl_Factory implements Factory<LocationRepositoryImpl> {
  private final Provider<ApiService> apiProvider;

  public LocationRepositoryImpl_Factory(Provider<ApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public LocationRepositoryImpl get() {
    return newInstance(apiProvider.get());
  }

  public static LocationRepositoryImpl_Factory create(Provider<ApiService> apiProvider) {
    return new LocationRepositoryImpl_Factory(apiProvider);
  }

  public static LocationRepositoryImpl newInstance(ApiService api) {
    return new LocationRepositoryImpl(api);
  }
}
