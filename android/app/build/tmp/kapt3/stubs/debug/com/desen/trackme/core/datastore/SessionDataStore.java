package com.desen.trackme.core.datastore;

import android.content.Context;
import com.desen.trackme.core.session.Session;

/**
 * Persistent, encrypted-at-rest-when-device-is-encrypted storage for the JWT pair.
 * This is what gives the app its "login forever" behavior: the refresh token survives
 * app restarts and device reboots, and the app silently re-authenticates on startup.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001:\u0001\rB\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0005\u001a\u00020\u0006H\u0086@\u00a2\u0006\u0002\u0010\u0007J\u0010\u0010\b\u001a\u0004\u0018\u00010\tH\u0086@\u00a2\u0006\u0002\u0010\u0007J\u0016\u0010\n\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u0010\fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/desen/trackme/core/datastore/SessionDataStore;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "clear", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "loadSession", "Lcom/desen/trackme/core/session/Session;", "saveSession", "session", "(Lcom/desen/trackme/core/session/Session;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Keys", "app_debug"})
public final class SessionDataStore {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    
    public SessionDataStore(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object saveSession(@org.jetbrains.annotations.NotNull()
    com.desen.trackme.core.session.Session session, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object loadSession(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.desen.trackme.core.session.Session> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object clear(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u000b\b\u00c2\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\u0007R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\u0007R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u0007R\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0007\u00a8\u0006\u0010"}, d2 = {"Lcom/desen/trackme/core/datastore/SessionDataStore$Keys;", "", "()V", "ACCESS_TOKEN", "Landroidx/datastore/preferences/core/Preferences$Key;", "", "getACCESS_TOKEN", "()Landroidx/datastore/preferences/core/Preferences$Key;", "DISPLAY_NAME", "getDISPLAY_NAME", "EMAIL", "getEMAIL", "REFRESH_TOKEN", "getREFRESH_TOKEN", "USER_ID", "getUSER_ID", "app_debug"})
    static final class Keys {
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> ACCESS_TOKEN = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> REFRESH_TOKEN = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> USER_ID = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> EMAIL = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> DISPLAY_NAME = null;
        @org.jetbrains.annotations.NotNull()
        public static final com.desen.trackme.core.datastore.SessionDataStore.Keys INSTANCE = null;
        
        private Keys() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getACCESS_TOKEN() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getREFRESH_TOKEN() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getUSER_ID() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getEMAIL() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getDISPLAY_NAME() {
            return null;
        }
    }
}