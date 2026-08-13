package com.desen.trackme.core.di;

import com.desen.trackme.data.repository.AuthRepositoryImpl;
import com.desen.trackme.data.repository.LocationRepositoryImpl;
import com.desen.trackme.domain.repository.AuthRepository;
import com.desen.trackme.domain.repository.LocationRepository;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\'\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\'J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0005\u001a\u00020\tH\'\u00a8\u0006\n"}, d2 = {"Lcom/desen/trackme/core/di/RepositoryModule;", "", "()V", "bindAuthRepository", "Lcom/desen/trackme/domain/repository/AuthRepository;", "impl", "Lcom/desen/trackme/data/repository/AuthRepositoryImpl;", "bindLocationRepository", "Lcom/desen/trackme/domain/repository/LocationRepository;", "Lcom/desen/trackme/data/repository/LocationRepositoryImpl;", "app_debug"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public abstract class RepositoryModule {
    
    public RepositoryModule() {
        super();
    }
    
    @dagger.Binds()
    @org.jetbrains.annotations.NotNull()
    public abstract com.desen.trackme.domain.repository.AuthRepository bindAuthRepository(@org.jetbrains.annotations.NotNull()
    com.desen.trackme.data.repository.AuthRepositoryImpl impl);
    
    @dagger.Binds()
    @org.jetbrains.annotations.NotNull()
    public abstract com.desen.trackme.domain.repository.LocationRepository bindLocationRepository(@org.jetbrains.annotations.NotNull()
    com.desen.trackme.data.repository.LocationRepositoryImpl impl);
}