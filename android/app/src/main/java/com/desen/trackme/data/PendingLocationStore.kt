package com.desen.trackme.data

import android.content.Context
import com.desen.trackme.core.network.dto.LocationUpdateRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Offline queue for coordinates that failed to POST (no network / backend down).
 * Persisted to a JSON file in app-private storage and flushed by [SyncWorker].
 */
@Singleton
class PendingLocationStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val file = File(context.filesDir, "pending_locations.json")
    private val json = Json { ignoreUnknownKeys = true }
    private val mutex = Mutex()

    suspend fun enqueue(location: LocationUpdateRequest) = mutex.withLock {
        val list = read().toMutableList()
        list.add(location)
        write(list)
    }

    /** Atomically removes and returns everything currently queued. */
    suspend fun drain(): List<LocationUpdateRequest> = mutex.withLock {
        val list = read()
        write(emptyList())
        list
    }

    /** Re-queues items that failed to send, prepended to preserve ordering. */
    suspend fun restore(remaining: List<LocationUpdateRequest>) = mutex.withLock {
        val list = read().toMutableList()
        list.addAll(0, remaining)
        write(list)
    }

    suspend fun size(): Int = mutex.withLock { read().size }

    private fun read(): List<LocationUpdateRequest> {
        if (!file.exists()) return emptyList()
        return try {
            json.decodeFromString<List<LocationUpdateRequest>>(file.readText())
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun write(list: List<LocationUpdateRequest>) {
        try {
            file.writeText(json.encodeToString(list))
        } catch (e: Exception) {
            // Storage error — drop rather than crash the background service.
        }
    }
}
