package com.desen.trackme.core.session

import com.desen.trackme.core.datastore.SessionDataStore
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory mirror of [SessionDataStore] so that synchronous OkHttp interceptors
 * can read the current access/refresh tokens without blocking.
 */
@Singleton
class TokenStore @Inject constructor(
    private val sessionDataStore: SessionDataStore
) {
    @Volatile
    private var session: Session? = null

    @Volatile
    private var loaded = false

    private val mutex = Mutex()

    /** Loads the persisted session once (idempotent). */
    suspend fun load() {
        if (loaded) return
        mutex.withLock {
            if (loaded) return
            session = sessionDataStore.loadSession()
            loaded = true
        }
    }

    fun current(): Session? = session

    fun accessToken(): String? = session?.accessToken

    fun refreshToken(): String? = session?.refreshToken

    suspend fun update(newSession: Session) {
        mutex.withLock {
            session = newSession
            loaded = true
        }
        sessionDataStore.saveSession(newSession)
    }

    suspend fun clear() {
        mutex.withLock {
            session = null
            loaded = true
        }
        sessionDataStore.clear()
    }
}
