package com.desen.trackme.data.repository;

import com.desen.trackme.core.network.ApiService;
import com.desen.trackme.core.network.dto.LocationUpdateRequest;
import com.desen.trackme.domain.repository.LocationRepository;
import javax.inject.Inject;
import javax.inject.Singleton;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0096@\u00a2\u0006\u0002\u0010\tR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Lcom/desen/trackme/data/repository/LocationRepositoryImpl;", "Lcom/desen/trackme/domain/repository/LocationRepository;", "api", "Lcom/desen/trackme/core/network/ApiService;", "(Lcom/desen/trackme/core/network/ApiService;)V", "postLocation", "", "location", "Lcom/desen/trackme/core/network/dto/LocationUpdateRequest;", "(Lcom/desen/trackme/core/network/dto/LocationUpdateRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class LocationRepositoryImpl implements com.desen.trackme.domain.repository.LocationRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.desen.trackme.core.network.ApiService api = null;
    
    @javax.inject.Inject()
    public LocationRepositoryImpl(@org.jetbrains.annotations.NotNull()
    com.desen.trackme.core.network.ApiService api) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object postLocation(@org.jetbrains.annotations.NotNull()
    com.desen.trackme.core.network.dto.LocationUpdateRequest location, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
}