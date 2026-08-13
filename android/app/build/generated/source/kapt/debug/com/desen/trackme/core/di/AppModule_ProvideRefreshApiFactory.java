package com.desen.trackme.core.di;

import com.desen.trackme.core.network.RefreshApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.serialization.json.Json;

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
public final class AppModule_ProvideRefreshApiFactory implements Factory<RefreshApiService> {
  private final Provider<Json> jsonProvider;

  public AppModule_ProvideRefreshApiFactory(Provider<Json> jsonProvider) {
    this.jsonProvider = jsonProvider;
  }

  @Override
  public RefreshApiService get() {
    return provideRefreshApi(jsonProvider.get());
  }

  public static AppModule_ProvideRefreshApiFactory create(Provider<Json> jsonProvider) {
    return new AppModule_ProvideRefreshApiFactory(jsonProvider);
  }

  public static RefreshApiService provideRefreshApi(Json json) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideRefreshApi(json));
  }
}
