package com.desen.trackme.domain.repository

import com.desen.trackme.core.network.dto.LocationUpdateRequest

interface LocationRepository {
    /** Posts a location update. Returns true only on a successful backend response. */
    suspend fun postLocation(location: LocationUpdateRequest): Boolean
}
