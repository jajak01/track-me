package com.desen.trackme.data.repository

import com.desen.trackme.core.network.ApiService
import com.desen.trackme.core.network.dto.LocationUpdateRequest
import com.desen.trackme.domain.repository.LocationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val api: ApiService
) : LocationRepository {

    override suspend fun postLocation(location: LocationUpdateRequest): Boolean =
        try {
            api.updateLocation(location).success
        } catch (e: Exception) {
            false
        }
}
