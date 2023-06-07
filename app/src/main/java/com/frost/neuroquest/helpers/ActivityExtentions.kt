package com.frost.neuroquest.helpers

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.frost.neuroquest.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth

fun Activity.logEventAnalytics(message: String, name:String){
    val analytics = FirebaseAnalytics.getInstance(this)
    val bundle = Bundle()
    bundle.putString("message", message)
    analytics.logEvent(name, bundle)
}

fun Activity.createUser(email: String, pass: String)=
    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)

fun Activity.signIn(email: String, pass: String)=
    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)

fun Activity.logOut()= FirebaseAuth.getInstance().signOut()

fun Activity.signInWithCredential(credential: AuthCredential) =
    FirebaseAuth.getInstance().signInWithCredential(credential)

fun Activity.showAlert(){
    val builder = AlertDialog.Builder(this)
    builder.setTitle(getString(R.string.error))
    builder.setMessage(getString(R.string.error_message))
    builder.setPositiveButton("ok", null)
    val dialog = builder.create()
    dialog.show()
}

fun Activity.requestForegroundAndBackgroundLocationPermissions(runningQOrLater: Boolean= true) {
    val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 3 // random unique value
    val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 4
    if (foregroundAndBackgroundLocationPermissionApproved())
        return
    var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    val resultCode = when {
        runningQOrLater -> {
            permissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
            REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
        }
        else -> REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
    }
    ActivityCompat.requestPermissions(this, permissionsArray, resultCode)
}

fun Activity.hasLocationPermission(context: Context): Boolean {
    return !(ActivityCompat.checkSelfPermission(context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    ) != PackageManager.PERMISSION_GRANTED)
}

fun Activity.foregroundAndBackgroundLocationPermissionApproved(runningQOrLater: Boolean= true): Boolean {
    val foregroundLocationApproved = (
            PERMISSION_GRANTED ==
                    ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION))
    val backgroundPermissionApproved =
        if (runningQOrLater) {
            PERMISSION_GRANTED ==
                    ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else {
            true
        }
    return foregroundLocationApproved && backgroundPermissionApproved
}

