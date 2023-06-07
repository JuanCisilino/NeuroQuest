package com.frost.neuroquest

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.frost.neuroquest.CurrentUser.Companion.nombre
import com.frost.neuroquest.model.Personaje
import com.frost.neuroquest.model.Places
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_GEOFENCE_EVENT) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)

            when {
                geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL -> doOnTransitionDwell(context, geofencingEvent)
                geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER -> doOnTransitionEnter(context, geofencingEvent)
                geofencingEvent.hasError() -> doOnError(context, geofencingEvent)
            }
        }
    }

    private fun doOnTransitionDwell(context: Context, geofencingEvent: GeofencingEvent) {
        Toast.makeText(context, "Esta en una geofenza", Toast.LENGTH_SHORT).show()
        val fenceId = getFenceId(geofencingEvent)
        val foundIndex = CurrentUser.lugares.indexOfFirst { it.id == fenceId.toInt() }
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.sendGeofenceEnteredNotification(context, foundIndex)
    }

    private fun doOnTransitionEnter(context: Context, geofencingEvent: GeofencingEvent) {

        val fenceId = getFenceId(geofencingEvent)

        // Check geofence against the constants listed in GeofenceUtil.kt to see if the
        // user has entered any of the locations we track for geofences.
        val foundIndex = CurrentUser.lugares.indexOfFirst { it.id == fenceId.toInt() }

        val lugar = CurrentUser.lugares[foundIndex]
        CurrentUser.disponibles.add(lugar)
        CurrentUser.puntos.add(lugar.id)
        CurrentUser.persSaved = false
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

    private fun doOnError(context: Context, geofencingEvent: GeofencingEvent) {
        val errorMessage = errorMessage(context, geofencingEvent.errorCode)
        Log.e(TAG, errorMessage)
    }

    private fun getFenceId(geofencingEvent: GeofencingEvent) =
        when {
            geofencingEvent.triggeringGeofences.isNotEmpty() ->
                geofencingEvent.triggeringGeofences[0].requestId
            else -> {
                Log.e(TAG, "No Geofence Trigger Found! Abort mission!")
                ""
            }
        }

    companion object {
        internal const val ACTION_GEOFENCE_EVENT =
            "Neuroquest.action.ACTION_GEOFENCE_EVENT"
    }
}

private const val TAG = "GeofenceReceiver"