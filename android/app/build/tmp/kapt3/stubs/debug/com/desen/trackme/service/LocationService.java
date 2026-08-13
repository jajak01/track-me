package com.desen.trackme.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.desen.trackme.MainActivity;
import com.desen.trackme.R;
import com.desen.trackme.core.network.dto.LocationUpdateRequest;
import com.desen.trackme.data.PendingLocationStore;
import com.desen.trackme.domain.repository.LocationRepository;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.Priority;
import dagger.hilt.android.AndroidEntryPoint;
import kotlinx.coroutines.Dispatchers;
import java.time.Instant;
import javax.inject.Inject;

/**
 * Persistent ForegroundService that records GPS fixes via the Fused Location
 * Provider and POSTs them to the backend on a fixed interval. It renders
 * nothing — this is the "headless" telemetry engine from plan.txt.
 */
@dagger.hilt.android.AndroidEntryPoint()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\b\u0007\u0018\u0000 12\u00020\u0001:\u00011B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0019\u001a\u00020\u001aH\u0002J\b\u0010\u001b\u001a\u00020\u001cH\u0002J\b\u0010\u001d\u001a\u00020\u001eH\u0002J\u0010\u0010\u001f\u001a\u00020\u001e2\u0006\u0010 \u001a\u00020!H\u0002J\b\u0010\"\u001a\u00020#H\u0002J\b\u0010$\u001a\u00020#H\u0002J\b\u0010%\u001a\u00020\u001eH\u0002J\u0014\u0010&\u001a\u0004\u0018\u00010\'2\b\u0010(\u001a\u0004\u0018\u00010)H\u0016J\b\u0010*\u001a\u00020\u001eH\u0016J\"\u0010+\u001a\u00020\u001a2\b\u0010(\u001a\u0004\u0018\u00010)2\u0006\u0010,\u001a\u00020\u001a2\u0006\u0010-\u001a\u00020\u001aH\u0016J\b\u0010.\u001a\u00020\u001eH\u0002J\b\u0010/\u001a\u00020\u001eH\u0002J\b\u00100\u001a\u00020\u001eH\u0002R\u001e\u0010\u0003\u001a\u00020\u00048\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u000b\u001a\u00020\f8\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u001e\u0010\u0011\u001a\u00020\u00128\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016R\u000e\u0010\u0017\u001a\u00020\u0018X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00062"}, d2 = {"Lcom/desen/trackme/service/LocationService;", "Landroid/app/Service;", "()V", "fusedLocationClient", "Lcom/google/android/gms/location/FusedLocationProviderClient;", "getFusedLocationClient", "()Lcom/google/android/gms/location/FusedLocationProviderClient;", "setFusedLocationClient", "(Lcom/google/android/gms/location/FusedLocationProviderClient;)V", "locationCallback", "Lcom/google/android/gms/location/LocationCallback;", "locationRepository", "Lcom/desen/trackme/domain/repository/LocationRepository;", "getLocationRepository", "()Lcom/desen/trackme/domain/repository/LocationRepository;", "setLocationRepository", "(Lcom/desen/trackme/domain/repository/LocationRepository;)V", "pendingStore", "Lcom/desen/trackme/data/PendingLocationStore;", "getPendingStore", "()Lcom/desen/trackme/data/PendingLocationStore;", "setPendingStore", "(Lcom/desen/trackme/data/PendingLocationStore;)V", "serviceScope", "Lkotlinx/coroutines/CoroutineScope;", "batteryLevel", "", "buildNotification", "Landroid/app/Notification;", "createChannel", "", "handleLocation", "location", "Landroid/location/Location;", "hasLocationPermission", "", "isCharging", "notifySynced", "onBind", "Landroid/os/IBinder;", "intent", "Landroid/content/Intent;", "onDestroy", "onStartCommand", "flags", "startId", "startForegroundCompat", "startTracking", "stopTracking", "Companion", "app_debug"})
public final class LocationService extends android.app.Service {
    @javax.inject.Inject()
    public com.desen.trackme.domain.repository.LocationRepository locationRepository;
    @javax.inject.Inject()
    public com.desen.trackme.data.PendingLocationStore pendingStore;
    @javax.inject.Inject()
    public com.google.android.gms.location.FusedLocationProviderClient fusedLocationClient;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope serviceScope = null;
    @org.jetbrains.annotations.Nullable()
    private com.google.android.gms.location.LocationCallback locationCallback;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_START = "com.desen.trackme.action.START";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_STOP = "com.desen.trackme.action.STOP";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_LOCATION_SYNCED = "com.desen.trackme.action.LOCATION_SYNCED";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String CHANNEL_ID = "location_tracking";
    public static final int NOTIFICATION_ID = 1001;
    public static final long LOCATION_INTERVAL_MS = 5000L;
    @kotlin.jvm.Volatile()
    private static volatile boolean isRunning = false;
    @org.jetbrains.annotations.NotNull()
    public static final com.desen.trackme.service.LocationService.Companion Companion = null;
    
    public LocationService() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.desen.trackme.domain.repository.LocationRepository getLocationRepository() {
        return null;
    }
    
    public final void setLocationRepository(@org.jetbrains.annotations.NotNull()
    com.desen.trackme.domain.repository.LocationRepository p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.desen.trackme.data.PendingLocationStore getPendingStore() {
        return null;
    }
    
    public final void setPendingStore(@org.jetbrains.annotations.NotNull()
    com.desen.trackme.data.PendingLocationStore p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.google.android.gms.location.FusedLocationProviderClient getFusedLocationClient() {
        return null;
    }
    
    public final void setFusedLocationClient(@org.jetbrains.annotations.NotNull()
    com.google.android.gms.location.FusedLocationProviderClient p0) {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public android.os.IBinder onBind(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent) {
        return null;
    }
    
    @java.lang.Override()
    public int onStartCommand(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent, int flags, int startId) {
        return 0;
    }
    
    private final void startTracking() {
    }
    
    private final void handleLocation(android.location.Location location) {
    }
    
    private final void stopTracking() {
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
    
    private final boolean hasLocationPermission() {
        return false;
    }
    
    private final void startForegroundCompat() {
    }
    
    private final void createChannel() {
    }
    
    private final android.app.Notification buildNotification() {
        return null;
    }
    
    private final int batteryLevel() {
        return 0;
    }
    
    private final boolean isCharging() {
        return false;
    }
    
    private final void notifySynced() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\u000e\u0010\u0014\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0086T\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u000e\u001a\u00020\r2\u0006\u0010\f\u001a\u00020\r@BX\u0086\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f\u00a8\u0006\u0015"}, d2 = {"Lcom/desen/trackme/service/LocationService$Companion;", "", "()V", "ACTION_LOCATION_SYNCED", "", "ACTION_START", "ACTION_STOP", "CHANNEL_ID", "LOCATION_INTERVAL_MS", "", "NOTIFICATION_ID", "", "<set-?>", "", "isRunning", "()Z", "start", "", "context", "Landroid/content/Context;", "stop", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        public final boolean isRunning() {
            return false;
        }
        
        public final void start(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
        }
        
        public final void stop(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
        }
    }
}