package com.trackme.app.data.repository;

import com.trackme.app.data.api.UserApi;
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
public final class UserRepository_Factory implements Factory<UserRepository> {
  private final Provider<UserApi> apiProvider;

  public UserRepository_Factory(Provider<UserApi> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public UserRepository get() {
    return newInstance(apiProvider.get());
  }

  public static UserRepository_Factory create(Provider<UserApi> apiProvider) {
    return new UserRepository_Factory(apiProvider);
  }

  public static UserRepository newInstance(UserApi api) {
    return new UserRepository(api);
  }
}
