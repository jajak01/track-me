package com.desen.trackme.core.network;

import com.desen.trackme.core.network.dto.ApiResponse;
import com.desen.trackme.core.network.dto.LocationResponse;
import com.desen.trackme.core.network.dto.LocationUpdateRequest;
import com.desen.trackme.core.network.dto.LoginRequest;
import com.desen.trackme.core.network.dto.LoginResponse;
import com.desen.trackme.core.network.dto.RefreshRequest;
import com.desen.trackme.core.network.dto.RegisterRequest;
import com.desen.trackme.core.network.dto.UserResponse;
import retrofit2.http.Body;
import retrofit2.http.POST;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u001e\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007J\u001e\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u000bJ\u001e\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u000bJ\u001e\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u000fH\u00a7@\u00a2\u0006\u0002\u0010\u0010J\u001e\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0013H\u00a7@\u00a2\u0006\u0002\u0010\u0014\u00a8\u0006\u0015"}, d2 = {"Lcom/desen/trackme/core/network/ApiService;", "", "login", "Lcom/desen/trackme/core/network/dto/ApiResponse;", "Lcom/desen/trackme/core/network/dto/LoginResponse;", "request", "Lcom/desen/trackme/core/network/dto/LoginRequest;", "(Lcom/desen/trackme/core/network/dto/LoginRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "logout", "", "Lcom/desen/trackme/core/network/dto/RefreshRequest;", "(Lcom/desen/trackme/core/network/dto/RefreshRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "refresh", "register", "Lcom/desen/trackme/core/network/dto/UserResponse;", "Lcom/desen/trackme/core/network/dto/RegisterRequest;", "(Lcom/desen/trackme/core/network/dto/RegisterRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateLocation", "Lcom/desen/trackme/core/network/dto/LocationResponse;", "Lcom/desen/trackme/core/network/dto/LocationUpdateRequest;", "(Lcom/desen/trackme/core/network/dto/LocationUpdateRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface ApiService {
    
    @retrofit2.http.POST(value = "api/v1/auth/register")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object register(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.desen.trackme.core.network.dto.RegisterRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.desen.trackme.core.network.dto.ApiResponse<com.desen.trackme.core.network.dto.UserResponse>> $completion);
    
    @retrofit2.http.POST(value = "api/v1/auth/login")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object login(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.desen.trackme.core.network.dto.LoginRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.desen.trackme.core.network.dto.ApiResponse<com.desen.trackme.core.network.dto.LoginResponse>> $completion);
    
    @retrofit2.http.POST(value = "api/v1/auth/refresh")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object refresh(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.desen.trackme.core.network.dto.RefreshRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.desen.trackme.core.network.dto.ApiResponse<com.desen.trackme.core.network.dto.LoginResponse>> $completion);
    
    @retrofit2.http.POST(value = "api/v1/auth/logout")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object logout(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.desen.trackme.core.network.dto.RefreshRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.desen.trackme.core.network.dto.ApiResponse<kotlin.Unit>> $completion);
    
    @retrofit2.http.POST(value = "api/v1/location/update")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateLocation(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.desen.trackme.core.network.dto.LocationUpdateRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.desen.trackme.core.network.dto.ApiResponse<com.desen.trackme.core.network.dto.LocationResponse>> $completion);
}