package com.desen.trackme.core.network;

import com.desen.trackme.core.network.dto.RefreshRequest;
import com.desen.trackme.core.session.Session;
import com.desen.trackme.core.session.TokenStore;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * On a 401 response, silently calls `POST /auth/refresh` with the stored refresh
 * token, persists the new pair, and retries the original request once. This is
 * what keeps the background service authenticated forever without user action.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u001c\u0010\u0007\u001a\u0004\u0018\u00010\b2\b\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000b\u001a\u00020\fH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/desen/trackme/core/network/TokenAuthenticator;", "Lokhttp3/Authenticator;", "tokenStore", "Lcom/desen/trackme/core/session/TokenStore;", "refreshApi", "Lcom/desen/trackme/core/network/RefreshApiService;", "(Lcom/desen/trackme/core/session/TokenStore;Lcom/desen/trackme/core/network/RefreshApiService;)V", "authenticate", "Lokhttp3/Request;", "route", "Lokhttp3/Route;", "response", "Lokhttp3/Response;", "responseCount", "", "app_debug"})
public final class TokenAuthenticator implements okhttp3.Authenticator {
    @org.jetbrains.annotations.NotNull()
    private final com.desen.trackme.core.session.TokenStore tokenStore = null;
    @org.jetbrains.annotations.NotNull()
    private final com.desen.trackme.core.network.RefreshApiService refreshApi = null;
    
    public TokenAuthenticator(@org.jetbrains.annotations.NotNull()
    com.desen.trackme.core.session.TokenStore tokenStore, @org.jetbrains.annotations.NotNull()
    com.desen.trackme.core.network.RefreshApiService refreshApi) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public okhttp3.Request authenticate(@org.jetbrains.annotations.Nullable()
    okhttp3.Route route, @org.jetbrains.annotations.NotNull()
    okhttp3.Response response) {
        return null;
    }
    
    private final int responseCount(okhttp3.Response response) {
        return 0;
    }
}