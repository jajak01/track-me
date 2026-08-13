package com.desen.trackme.core.di;

import com.desen.trackme.core.network.RefreshApiService;
import com.desen.trackme.core.network.TokenAuthenticator;
import com.desen.trackme.core.session.TokenStore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideTokenAuthenticatorFactory implements Factory<TokenAuthenticator> {
  private final Provider<TokenStore> tokenStoreProvider;

  private final Provider<RefreshApiService> refreshApiProvider;

  public AppModule_ProvideTokenAuthenticatorFactory(Provider<TokenStore> tokenStoreProvider,
      Provider<RefreshApiService> refreshApiProvider) {
    this.tokenStoreProvider = tokenStoreProvider;
    this.refreshApiProvider = refreshApiProvider;
  }

  @Override
  public TokenAuthenticator get() {
    return provideTokenAuthenticator(tokenStoreProvider.get(), refreshApiProvider.get());
  }

  public static AppModule_ProvideTokenAuthenticatorFactory create(
      Provider<TokenStore> tokenStoreProvider, Provider<RefreshApiService> refreshApiProvider) {
    return new AppModule_ProvideTokenAuthenticatorFactory(tokenStoreProvider, refreshApiProvider);
  }

  public static TokenAuthenticator provideTokenAuthenticator(TokenStore tokenStore,
      RefreshApiService refreshApi) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideTokenAuthenticator(tokenStore, refreshApi));
  }
}
