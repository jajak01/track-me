package com.trackme.app.ui.profile;

import com.trackme.app.data.local.TokenManager;
import com.trackme.app.data.repository.AuthRepository;
import com.trackme.app.data.repository.UserRepository;
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
public final class ProfileViewModel_Factory implements Factory<ProfileViewModel> {
  private final Provider<UserRepository> userRepositoryProvider;

  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<TokenManager> tokenManagerProvider;

  public ProfileViewModel_Factory(Provider<UserRepository> userRepositoryProvider,
      Provider<AuthRepository> authRepositoryProvider,
      Provider<TokenManager> tokenManagerProvider) {
    this.userRepositoryProvider = userRepositoryProvider;
    this.authRepositoryProvider = authRepositoryProvider;
    this.tokenManagerProvider = tokenManagerProvider;
  }

  @Override
  public ProfileViewModel get() {
    return newInstance(userRepositoryProvider.get(), authRepositoryProvider.get(), tokenManagerProvider.get());
  }

  public static ProfileViewModel_Factory create(Provider<UserRepository> userRepositoryProvider,
      Provider<AuthRepository> authRepositoryProvider,
      Provider<TokenManager> tokenManagerProvider) {
    return new ProfileViewModel_Factory(userRepositoryProvider, authRepositoryProvider, tokenManagerProvider);
  }

  public static ProfileViewModel newInstance(UserRepository userRepository,
      AuthRepository authRepository, TokenManager tokenManager) {
    return new ProfileViewModel(userRepository, authRepository, tokenManager);
  }
}
