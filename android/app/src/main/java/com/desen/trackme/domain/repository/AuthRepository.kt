package com.desen.trackme.domain.repository

import com.desen.trackme.core.session.Session

interface AuthRepository {
    /** Registers then immediately logs in, returning a persisted [Session]. */
    suspend fun register(email: String, password: String, displayName: String): Result<Session>

    /** Logs in and persists the token pair for "login forever". */
    suspend fun login(email: String, password: String): Result<Session>

    suspend fun logout()

    suspend fun isLoggedIn(): Boolean

    suspend fun currentSession(): Session?
}
