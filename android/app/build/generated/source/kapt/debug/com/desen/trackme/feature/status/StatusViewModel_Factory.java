package com.desen.trackme.feature.status;

import com.desen.trackme.domain.repository.AuthRepository;
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
    "cast"
})
public final class StatusViewModel_Factory implements Factory<StatusViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  public StatusViewModel_Factory(Provider<AuthRepository> authRepositoryProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public StatusViewModel get() {
    return newInstance(authRepositoryProvider.get());
  }

  public static StatusViewModel_Factory create(Provider<AuthRepository> authRepositoryProvider) {
    return new StatusViewModel_Factory(authRepositoryProvider);
  }

  public static StatusViewModel newInstance(AuthRepository authRepository) {
    return new StatusViewModel(authRepository);
  }
}
