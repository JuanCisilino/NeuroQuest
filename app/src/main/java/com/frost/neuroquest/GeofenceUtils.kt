package com.frost.neuroquest

import android.content.Context
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.maps.model.LatLng
import java.util.concurrent.TimeUnit

fun errorMessage(context: Context, errorCode: Int): String {
    return when (errorCode) {
        GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> "Geofensa no disponible"
        GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> "Demasiadas geofenzas"
        GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> "Demasiados pendientes"
        else -> "Error desconocido wuuuu!!"
    }
}

/**
 * Stores latitude and longitude information along with a hint to help user find the location.
 */
data class LandmarkDataObject(val id: String, val hint: Int, val name: Int, val latLong: LatLng)

internal object GeofencingConstants {

    /**
     * Used to set an expiration time for a geofence. After this amount of time, Location services
     * stops tracking the geofence. For this sample, geofences expire after one hour.
     */
    val GEOFENCE_EXPIRATION_IN_MILLISECONDS: Long = TimeUnit.HOURS.toMillis(1)


    val NUM_LANDMARKS = CurrentUser.lugares.size
    const val GEOFENCE_RADIUS_IN_METERS = 100f
    const val EXTRA_GEOFENCE_INDEX = "GEOFENCE_INDEX"
}
