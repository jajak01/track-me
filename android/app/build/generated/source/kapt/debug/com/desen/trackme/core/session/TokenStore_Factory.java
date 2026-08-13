package com.desen.trackme.core.session;

import com.desen.trackme.core.datastore.SessionDataStore;
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
public final class TokenStore_Factory implements Factory<TokenStore> {
  private final Provider<SessionDataStore> sessionDataStoreProvider;

  public TokenStore_Factory(Provider<SessionDataStore> sessionDataStoreProvider) {
    this.sessionDataStoreProvider = sessionDataStoreProvider;
  }

  @Override
  public TokenStore get() {
    return newInstance(sessionDataStoreProvider.get());
  }

  public static TokenStore_Factory create(Provider<SessionDataStore> sessionDataStoreProvider) {
    return new TokenStore_Factory(sessionDataStoreProvider);
  }

  public static TokenStore newInstance(SessionDataStore sessionDataStore) {
    return new TokenStore(sessionDataStore);
  }
}
