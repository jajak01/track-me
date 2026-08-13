package com.desen.trackme.core.di;

import com.desen.trackme.core.network.AuthInterceptor;
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
public final class AppModule_ProvideAuthInterceptorFactory implements Factory<AuthInterceptor> {
  private final Provider<TokenStore> tokenStoreProvider;

  public AppModule_ProvideAuthInterceptorFactory(Provider<TokenStore> tokenStoreProvider) {
    this.tokenStoreProvider = tokenStoreProvider;
  }

  @Override
  public AuthInterceptor get() {
    return provideAuthInterceptor(tokenStoreProvider.get());
  }

  public static AppModule_ProvideAuthInterceptorFactory create(
      Provider<TokenStore> tokenStoreProvider) {
    return new AppModule_ProvideAuthInterceptorFactory(tokenStoreProvider);
  }

  public static AuthInterceptor provideAuthInterceptor(TokenStore tokenStore) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideAuthInterceptor(tokenStore));
  }
}
