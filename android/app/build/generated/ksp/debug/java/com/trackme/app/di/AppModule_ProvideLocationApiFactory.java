package com.trackme.app.di;

import com.trackme.app.data.api.LocationApi;
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
public final class AppModule_ProvideLocationApiFactory implements Factory<LocationApi> {
  private final Provider<Retrofit> retrofitProvider;

  public AppModule_ProvideLocationApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public LocationApi get() {
    return provideLocationApi(retrofitProvider.get());
  }

  public static AppModule_ProvideLocationApiFactory create(Provider<Retrofit> retrofitProvider) {
    return new AppModule_ProvideLocationApiFactory(retrofitProvider);
  }

  public static LocationApi provideLocationApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideLocationApi(retrofit));
  }
}
