package com.desen.trackme.core.network;

import com.desen.trackme.core.network.dto.ApiResponse;
import com.desen.trackme.core.network.dto.LoginResponse;
import com.desen.trackme.core.network.dto.RefreshRequest;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Minimal API surface used only by the OkHttp [Authenticator] to rotate tokens.
 * It must NOT carry the authenticator itself, otherwise a 401 during refresh
 * would recurse forever.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u001e\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007\u00a8\u0006\b"}, d2 = {"Lcom/desen/trackme/core/network/RefreshApiService;", "", "refresh", "Lcom/desen/trackme/core/network/dto/ApiResponse;", "Lcom/desen/trackme/core/network/dto/LoginResponse;", "request", "Lcom/desen/trackme/core/network/dto/RefreshRequest;", "(Lcom/desen/trackme/core/network/dto/RefreshRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface RefreshApiService {
    
    @retrofit2.http.POST(value = "api/v1/auth/refresh")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object refresh(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.desen.trackme.core.network.dto.RefreshRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.desen.trackme.core.network.dto.ApiResponse<com.desen.trackme.core.network.dto.LoginResponse>> $completion);
}