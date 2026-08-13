package com.desen.trackme.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.desen.trackme.core.network.dto.LocationUpdateRequest
import com.desen.trackme.data.PendingLocationStore
import com.desen.trackme.domain.repository.LocationRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

/**
 * Flushes the offline coordinate queue to the backend when network is available.
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val locationRepository: LocationRepository,
    private val pendingStore: PendingLocationStore
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val pending = pendingStore.drain()
        if (pending.isEmpty()) return Result.success()

        val remaining = mutableListOf<LocationUpdateRequest>()
        for (location in pending) {
            if (!locationRepository.postLocation(location)) {
                remaining.add(location)
            }
        }

        if (remaining.isNotEmpty()) {
            pendingStore.restore(remaining)
            return Result.retry()
        }
        return Result.success()
    }

    companion object {
        fun enqueue(context: Context) {
            val request = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build()
            WorkManager.getInstance(context).enqueue(request)
        }
    }
}
