package com.desen.trackme.data;

import android.content.Context;
import com.desen.trackme.core.network.dto.LocationUpdateRequest;
import dagger.hilt.android.qualifiers.ApplicationContext;
import java.io.File;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Offline queue for coordinates that failed to POST (no network / backend down).
 * Persisted to a JSON file in app-private storage and flushed by [SyncWorker].
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0011\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fH\u0086@\u00a2\u0006\u0002\u0010\u000eJ\u0016\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\rH\u0086@\u00a2\u0006\u0002\u0010\u0012J\u000e\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\r0\fH\u0002J\u001c\u0010\u0014\u001a\u00020\u00102\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\r0\fH\u0086@\u00a2\u0006\u0002\u0010\u0016J\u000e\u0010\u0017\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010\u000eJ\u0016\u0010\u0019\u001a\u00020\u00102\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\r0\fH\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001b"}, d2 = {"Lcom/desen/trackme/data/PendingLocationStore;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "file", "Ljava/io/File;", "json", "Lkotlinx/serialization/json/Json;", "mutex", "Lkotlinx/coroutines/sync/Mutex;", "drain", "", "Lcom/desen/trackme/core/network/dto/LocationUpdateRequest;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "enqueue", "", "location", "(Lcom/desen/trackme/core/network/dto/LocationUpdateRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "read", "restore", "remaining", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "size", "", "write", "list", "app_debug"})
public final class PendingLocationStore {
    @org.jetbrains.annotations.NotNull()
    private final java.io.File file = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.serialization.json.Json json = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.sync.Mutex mutex = null;
    
    @javax.inject.Inject()
    public PendingLocationStore(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object enqueue(@org.jetbrains.annotations.NotNull()
    com.desen.trackme.core.network.dto.LocationUpdateRequest location, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Atomically removes and returns everything currently queued.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object drain(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.desen.trackme.core.network.dto.LocationUpdateRequest>> $completion) {
        return null;
    }
    
    /**
     * Re-queues items that failed to send, prepended to preserve ordering.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object restore(@org.jetbrains.annotations.NotNull()
    java.util.List<com.desen.trackme.core.network.dto.LocationUpdateRequest> remaining, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object size(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
    
    private final java.util.List<com.desen.trackme.core.network.dto.LocationUpdateRequest> read() {
        return null;
    }
    
    private final void write(java.util.List<com.desen.trackme.core.network.dto.LocationUpdateRequest> list) {
    }
}