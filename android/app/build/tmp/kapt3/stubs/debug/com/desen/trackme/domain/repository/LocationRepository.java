package com.desen.trackme.domain.repository;

import com.desen.trackme.core.network.dto.LocationUpdateRequest;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/desen/trackme/domain/repository/LocationRepository;", "", "postLocation", "", "location", "Lcom/desen/trackme/core/network/dto/LocationUpdateRequest;", "(Lcom/desen/trackme/core/network/dto/LocationUpdateRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface LocationRepository {
    
    /**
     * Posts a location update. Returns true only on a successful backend response.
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object postLocation(@org.jetbrains.annotations.NotNull()
    com.desen.trackme.core.network.dto.LocationUpdateRequest location, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion);
}