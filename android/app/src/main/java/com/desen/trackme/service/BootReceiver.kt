package com.desen.trackme.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.desen.trackme.core.datastore.SessionDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Restarts the background location service after a device reboot,
 * but only if the user is still logged in (persistent session).
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action != Intent.ACTION_BOOT_COMPLETED &&
            action != "android.intent.action.QUICKBOOT_POWERON"
        ) {
            return
        }

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            val session = SessionDataStore(context).loadSession()
            if (session != null) {
                LocationService.start(context)
                SyncWorker.enqueue(context)
            }
        }
    }
}
