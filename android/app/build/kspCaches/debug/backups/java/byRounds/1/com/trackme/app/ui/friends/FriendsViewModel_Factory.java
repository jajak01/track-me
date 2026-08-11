package com.trackme.app.ui.friends;

import com.trackme.app.data.repository.FriendRepository;
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
public final class FriendsViewModel_Factory implements Factory<FriendsViewModel> {
  private final Provider<FriendRepository> friendRepositoryProvider;

  public FriendsViewModel_Factory(Provider<FriendRepository> friendRepositoryProvider) {
    this.friendRepositoryProvider = friendRepositoryProvider;
  }

  @Override
  public FriendsViewModel get() {
    return newInstance(friendRepositoryProvider.get());
  }

  public static FriendsViewModel_Factory create(
      Provider<FriendRepository> friendRepositoryProvider) {
    return new FriendsViewModel_Factory(friendRepositoryProvider);
  }

  public static FriendsViewModel newInstance(FriendRepository friendRepository) {
    return new FriendsViewModel(friendRepository);
  }
}
