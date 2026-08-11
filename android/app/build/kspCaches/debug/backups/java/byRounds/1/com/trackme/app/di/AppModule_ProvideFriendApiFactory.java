package com.trackme.app.di;

import com.trackme.app.data.api.FriendApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

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
public final class AppModule_ProvideFriendApiFactory implements Factory<FriendApi> {
  private final Provider<Retrofit> retrofitProvider;

  public AppModule_ProvideFriendApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public FriendApi get() {
    return provideFriendApi(retrofitProvider.get());
  }

  public static AppModule_ProvideFriendApiFactory create(Provider<Retrofit> retrofitProvider) {
    return new AppModule_ProvideFriendApiFactory(retrofitProvider);
  }

  public static FriendApi provideFriendApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideFriendApi(retrofit));
  }
}
