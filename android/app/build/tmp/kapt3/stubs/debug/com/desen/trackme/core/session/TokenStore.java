package com.desen.trackme.core.session;

import com.desen.trackme.core.datastore.SessionDataStore;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * In-memory mirror of [SessionDataStore] so that synchronous OkHttp interceptors
 * can read the current access/refresh tokens without blocking.
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\b\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u000b\u001a\u0004\u0018\u00010\fJ\u000e\u0010\r\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u000fJ\b\u0010\u0010\u001a\u0004\u0018\u00010\nJ\u000e\u0010\u0011\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u000fJ\b\u0010\u0012\u001a\u0004\u0018\u00010\fJ\u0016\u0010\u0013\u001a\u00020\u000e2\u0006\u0010\u0014\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u0015R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/desen/trackme/core/session/TokenStore;", "", "sessionDataStore", "Lcom/desen/trackme/core/datastore/SessionDataStore;", "(Lcom/desen/trackme/core/datastore/SessionDataStore;)V", "loaded", "", "mutex", "Lkotlinx/coroutines/sync/Mutex;", "session", "Lcom/desen/trackme/core/session/Session;", "accessToken", "", "clear", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "current", "load", "refreshToken", "update", "newSession", "(Lcom/desen/trackme/core/session/Session;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class TokenStore {
    @org.jetbrains.annotations.NotNull()
    private final com.desen.trackme.core.datastore.SessionDataStore sessionDataStore = null;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private volatile com.desen.trackme.core.session.Session session;
    @kotlin.jvm.Volatile()
    private volatile boolean loaded = false;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.sync.Mutex mutex = null;
    
    @javax.inject.Inject()
    public TokenStore(@org.jetbrains.annotations.NotNull()
    com.desen.trackme.core.datastore.SessionDataStore sessionDataStore) {
        super();
    }
    
    /**
     * Loads the persisted session once (idempotent).
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object load(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.desen.trackme.core.session.Session current() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String accessToken() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String refreshToken() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.desen.trackme.core.session.Session newSession, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object clear(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}