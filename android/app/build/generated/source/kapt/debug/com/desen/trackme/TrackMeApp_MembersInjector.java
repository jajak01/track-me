package com.desen.trackme;

import androidx.hilt.work.HiltWorkerFactory;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class TrackMeApp_MembersInjector implements MembersInjector<TrackMeApp> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public TrackMeApp_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<TrackMeApp> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new TrackMeApp_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(TrackMeApp instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.desen.trackme.TrackMeApp.workerFactory")
  public static void injectWorkerFactory(TrackMeApp instance, HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
