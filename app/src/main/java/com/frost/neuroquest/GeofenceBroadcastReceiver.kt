package com.frost.neuroquest

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_GEOFENCE_EVENT) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)

            if (geofencingEvent.hasError()) {
                val errorMessage = errorMessage(context, geofencingEvent.errorCode)
                Log.e(TAG, errorMessage)
                return
            }

            if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

                val fenceId = when {
                    geofencingEvent.triggeringGeofences.isNotEmpty() ->
                        geofencingEvent.triggeringGeofences[0].requestId
                    else -> {
                        Log.e(TAG, "No Geofence Trigger Found! Abort mission!")
                        return
                    }
                }
                // Check geofence against the constants listed in GeofenceUtil.kt to see if the
                // user has entered any of the locations we track for geofences.
                val foundIndex = CurrentUser.lugares.indexOfFirst {
                    it.id == fenceId.toInt()
                }

                val lugar = CurrentUser.lugares[foundIndex]
                CurrentUser.disponibles.add(lugar)
                CurrentUser.puntos.add(lugar.id)
                Toast.makeText(context, "Ingreso a ${lugar.nombre}", Toast.LENGTH_SHORT)
                    .show()

                // Unknown Geofences aren't helpful to us
                if ( -1 == foundIndex ) {
                    Log.e(TAG, "Unknown Geofence: Abort Mission")
                    return
                }

                val notificationManager = ContextCompat.getSystemService(
                    context,
                    NotificationManager::class.java
                ) as NotificationManager

                notificationManager.sendGeofenceEnteredNotification(
                    context, foundIndex
                )
            }

            if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
                Toast.makeText(context, "Esta en una geofenza", Toast.LENGTH_SHORT)
                    .show()
                val fenceId = when {
                    geofencingEvent.triggeringGeofences.isNotEmpty() ->
                        geofencingEvent.triggeringGeofences[0].requestId
                    else -> {
                        Log.e(TAG, "No Geofence Trigger Found! Abort mission!")
                        return
                    }
                }
                val foundIndex = CurrentUser.lugares.indexOfFirst {
                    it.id == fenceId.toInt()
                }
                val notificationManager = ContextCompat.getSystemService(
                    context,
                    NotificationManager::class.java
                ) as NotificationManager

                notificationManager.sendGeofenceEnteredNotification(
                    context, foundIndex
                )
            }
        }
    }
    companion object {
        internal const val ACTION_GEOFENCE_EVENT =
            "Neuroquest.action.ACTION_GEOFENCE_EVENT"
    }
}

private const val TAG = "GeofenceReceiver"