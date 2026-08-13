package com.desen.trackme.core.di;

import android.content.Context;
import com.desen.trackme.BuildConfig;
import com.desen.trackme.core.datastore.SessionDataStore;
import com.desen.trackme.core.network.ApiService;
import com.desen.trackme.core.network.AuthInterceptor;
import com.desen.trackme.core.network.RefreshApiService;
import com.desen.trackme.core.network.TokenAuthenticator;
import com.desen.trackme.core.session.TokenStore;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;

@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0007J\u0012\u0010\u000b\u001a\u00020\f2\b\b\u0001\u0010\r\u001a\u00020\u000eH\u0007J\b\u0010\u000f\u001a\u00020\u0010H\u0007J\u0018\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\b2\u0006\u0010\u0014\u001a\u00020\u0015H\u0007J\u0010\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0010H\u0007J\u0018\u0010\u0019\u001a\u00020\u00062\u0006\u0010\u0018\u001a\u00020\u00102\u0006\u0010\u001a\u001a\u00020\u0012H\u0007J\u0012\u0010\u001b\u001a\u00020\u001c2\b\b\u0001\u0010\r\u001a\u00020\u000eH\u0007J\u0018\u0010\u001d\u001a\u00020\u00152\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u001e\u001a\u00020\u0017H\u0007\u00a8\u0006\u001f"}, d2 = {"Lcom/desen/trackme/core/di/AppModule;", "", "()V", "provideApiService", "Lcom/desen/trackme/core/network/ApiService;", "retrofit", "Lretrofit2/Retrofit;", "provideAuthInterceptor", "Lcom/desen/trackme/core/network/AuthInterceptor;", "tokenStore", "Lcom/desen/trackme/core/session/TokenStore;", "provideFusedLocationClient", "Lcom/google/android/gms/location/FusedLocationProviderClient;", "context", "Landroid/content/Context;", "provideJson", "Lkotlinx/serialization/json/Json;", "provideOkHttpClient", "Lokhttp3/OkHttpClient;", "authInterceptor", "authenticator", "Lcom/desen/trackme/core/network/TokenAuthenticator;", "provideRefreshApi", "Lcom/desen/trackme/core/network/RefreshApiService;", "json", "provideRetrofit", "client", "provideSessionDataStore", "Lcom/desen/trackme/core/datastore/SessionDataStore;", "provideTokenAuthenticator", "refreshApi", "app_debug"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public final class AppModule {
    @org.jetbrains.annotations.NotNull()
    public static final com.desen.trackme.core.di.AppModule INSTANCE = null;
    
    private AppModule() {
        super();
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.serialization.json.Json provideJson() {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.desen.trackme.core.datastore.SessionDataStore provideSessionDataStore(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.google.android.gms.location.FusedLocationProviderClient provideFusedLocationClient(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    /**
     * Plain client used only for token refresh — no authenticator, no recursion.
     */
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.desen.trackme.core.network.RefreshApiService provideRefreshApi(@org.jetbrains.annotations.NotNull()
    kotlinx.serialization.json.Json json) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.desen.trackme.core.network.TokenAuthenticator provideTokenAuthenticator(@org.jetbrains.annotations.NotNull()
    com.desen.trackme.core.session.TokenStore tokenStore, @org.jetbrains.annotations.NotNull()
    com.desen.trackme.core.network.RefreshApiService refreshApi) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.desen.trackme.core.network.AuthInterceptor provideAuthInterceptor(@org.jetbrains.annotations.NotNull()
    com.desen.trackme.core.session.TokenStore tokenStore) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final okhttp3.OkHttpClient provideOkHttpClient(@org.jetbrains.annotations.NotNull()
    com.desen.trackme.core.network.AuthInterceptor authInterceptor, @org.jetbrains.annotations.NotNull()
    com.desen.trackme.core.network.TokenAuthenticator authenticator) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final retrofit2.Retrofit provideRetrofit(@org.jetbrains.annotations.NotNull()
    kotlinx.serialization.json.Json json, @org.jetbrains.annotations.NotNull()
    okhttp3.OkHttpClient client) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.desen.trackme.core.network.ApiService provideApiService(@org.jetbrains.annotations.NotNull()
    retrofit2.Retrofit retrofit) {
        return null;
    }
}