package com.frost.neuroquest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat


fun createChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            "GeofenceStatus",

            NotificationManager.IMPORTANCE_HIGH
        )
            .apply {
                setShowBadge(false)
            }

        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.description = "Geofence status notification"

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }
}

/*
 * A Kotlin extension function for AndroidX's NotificationCompat that sends our Geofence
 * entered notification.  It sends a custom notification based on the name string associated
 * with the LANDMARK_DATA from GeofencingConstatns in the GeofenceUtils file.
 */
fun NotificationManager.sendGeofenceEnteredNotification(context: Context, foundIndex: Int) {
    val contentIntent = Intent(context, MainActivity::class.java)
    contentIntent.putExtra(GeofencingConstants.EXTRA_GEOFENCE_INDEX, foundIndex)
    val contentPendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
//    val mapImage = BitmapFactory.decodeResource(
//        context.resources,
//        R.drawable.map_small
//    )
//    val bigPicStyle = NotificationCompat.BigPictureStyle()
//        .bigPicture(mapImage)
//        .bigLargeIcon(null)

    // We use the name resource ID from the LANDMARK_DATA along with content_text to create
    // a custom message when a Geofence triggers.
    val lugar = CurrentUser.lugares[foundIndex].nombre
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle("nEUROqUEST")
        .setContentText("Bienvenido a $lugar")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(contentPendingIntent)
        .setSmallIcon(R.drawable.ic_notifications_black_24dp)


    notify(NOTIFICATION_ID, builder.build())
}

private const val NOTIFICATION_ID = 33
private const val CHANNEL_ID = "GeofenceChannel"


