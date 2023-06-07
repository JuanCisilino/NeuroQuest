package com.frost.neuroquest

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.fragment.app.Fragment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import pub.devrel.easypermissions.EasyPermissions

fun Fragment.requestPermission(){
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        EasyPermissions.requestPermissions(this, getString(R.string.allow), 0,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    } else {
        EasyPermissions.requestPermissions(this, getString(R.string.allow), 0,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    }
}

fun Fragment.logEventToCrashlytics(event: String){
    val exception = Exception(event)
    FirebaseCrashlytics.getInstance().recordException(exception)
}

fun Fragment.hasPermission(context: Context) =
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    } else {
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
    }

