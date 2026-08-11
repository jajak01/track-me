package com.trackme.app.data.repository;

import com.trackme.app.data.api.FriendApi;
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
public final class FriendRepository_Factory implements Factory<FriendRepository> {
  private final Provider<FriendApi> apiProvider;

  public FriendRepository_Factory(Provider<FriendApi> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public FriendRepository get() {
    return newInstance(apiProvider.get());
  }

  public static FriendRepository_Factory create(Provider<FriendApi> apiProvider) {
    return new FriendRepository_Factory(apiProvider);
  }

  public static FriendRepository newInstance(FriendApi api) {
    return new FriendRepository(api);
  }
}
