package com.trackme.app.data.api;

import com.trackme.app.data.local.TokenManager;
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
public final class WebSocketManager_Factory implements Factory<WebSocketManager> {
  private final Provider<TokenManager> tokenManagerProvider;

  public WebSocketManager_Factory(Provider<TokenManager> tokenManagerProvider) {
    this.tokenManagerProvider = tokenManagerProvider;
  }

  @Override
  public WebSocketManager get() {
    return newInstance(tokenManagerProvider.get());
  }

  public static WebSocketManager_Factory create(Provider<TokenManager> tokenManagerProvider) {
    return new WebSocketManager_Factory(tokenManagerProvider);
  }

  public static WebSocketManager newInstance(TokenManager tokenManager) {
    return new WebSocketManager(tokenManager);
  }
}
