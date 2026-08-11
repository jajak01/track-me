package com.trackme.app.data.location;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class LocationClient_Factory implements Factory<LocationClient> {
  private final Provider<Context> contextProvider;

  public LocationClient_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public LocationClient get() {
    return newInstance(contextProvider.get());
  }

  public static LocationClient_Factory create(Provider<Context> contextProvider) {
    return new LocationClient_Factory(contextProvider);
  }

  public static LocationClient newInstance(Context context) {
    return new LocationClient(context);
  }
}
