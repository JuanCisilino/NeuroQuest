package com.frost.neuroquest

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.frost.neuroquest.GeofenceBroadcastReceiver.Companion.ACTION_GEOFENCE_EVENT
import com.frost.neuroquest.databinding.ActivityMainBinding
import com.frost.neuroquest.helpers.*
import com.frost.neuroquest.model.Places
import com.frost.neuroquest.ui.LoadingDialog
import com.frost.neuroquest.ui.mapa.DashboardViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.google.zxing.integration.android.IntentIntegrator
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
    private val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 3 // random unique value
    private val GEOFENCE_RADIUS_IN_METERS = 200f
    private val GEOFENCE_EXPIRATION_IN_MILLISECONDS: Long = TimeUnit.HOURS.toMillis(1)
    private val REQUEST_TURN_DEVICE_LOCATION_ON = 29
    private val TAG = "NeuroQuestMainActivity"
    private val LOCATION_PERMISSION_INDEX = 0
    private val BACKGROUND_LOCATION_PERMISSION_INDEX = 1
    private lateinit var viewModel : DashboardViewModel
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var navController: NavController
    private val firebaseRemoteConfig = Firebase.remoteConfig
    private val gson = GsonBuilder().create()
    private val userPrefs = UserPrefs(this)
    private var loadingDialog = LoadingDialog(R.string.loading_message)

    companion object{
        fun start(context: Context){
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Create channel for notifications
        createChannel(this )
        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        geofencingClient = LocationServices.getGeofencingClient(this)
        setFirebaseRemoteConfig()

        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home,R.id.navigation_qr, R.id.navigation_dashboard)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun setFirebaseRemoteConfig() {
        loadingDialog.show(supportFragmentManager)
        setMinimalInterval()
        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            val exception = task.exception?.message
            when {
                task.isSuccessful -> getRemoteConfig()
                exception != null -> showAlert()
            }
            loadingDialog.dismiss()
        }
    }

    //TODO ELIMINAR ESTE METODO CUANDO TERMINE ETAPA DE PRUEBA
    private fun setMinimalInterval() {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(5)
            .build()
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
    }

    private fun getRemoteConfig(){
        val lugares = firebaseRemoteConfig.getString("lugares")
        if (CurrentUser.lugares.isEmpty()) CurrentUser.lugares = gson.fromJson(lugares, object : TypeToken<List<Places>>() {}.type)
        viewModel.lugares = CurrentUser.lugares
        viewModel.geofensas = filter(viewModel.lugares as ArrayList<Places>)
        CurrentUser.generateLatLongList()
        if (!hasLocationPermission(this)) requestForegroundAndBackgroundLocationPermissions()
        else checkDeviceLocationSettingsAndStartGeofence()
    }

    private fun filter(lugares: ArrayList<Places>): List<Places> {
        val toRemove = ArrayList<Places>()
        if (CurrentUser.puntos.isEmpty()) return listOf()
        CurrentUser.puntos.forEach { id ->
            val lugar = lugares.find { it.id == id }
            lugar?.let {
                CurrentUser.disponibles.add(it)
                toRemove.add(it) }
        }
        lugares.removeAll(toRemove)
        return lugares
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (
            grantResults.isEmpty() ||
            grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED ||
            (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
                    grantResults[BACKGROUND_LOCATION_PERMISSION_INDEX] ==
                    PackageManager.PERMISSION_DENIED))
        {
            Snackbar.make(
                binding.root,
                "No aceptaste y tenes que aceptar",
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction("Settings") {
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }.show()
        } else {
            checkDeviceLocationSettingsAndStartGeofence()
        }
    }

    private fun checkDeviceLocationSettingsAndStartGeofence(resolve:Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(this)
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(this,
                        REQUEST_TURN_DEVICE_LOCATION_ON)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(TAG, "Error geting location settings resolution: " + sendEx.message)
                }
            } else {
                Snackbar.make(
                    binding.root,
                    "No podemos cachar la ubicacion", Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettingsAndStartGeofence()
                }.show()
            }
        }

        locationSettingsResponseTask.addOnCompleteListener { if ( it.isSuccessful ) { setGeofences() } }
    }

    private fun setGeofences() {
        if (viewModel.geofenceIsActive()) return
        removeGeofences()

        if (viewModel.geofensas.isEmpty()) viewModel.lugares.forEach { lugar -> setGeofence(lugar) }
        else viewModel.geofensas.forEach { lugar -> setGeofence(lugar) }

    }

    private fun setGeofence(lugar: Places){
        val geofence = Geofence.Builder()
            .setRequestId(lugar.id.toString())
            .setCircularRegion(lugar.latitude, lugar.longitude, GEOFENCE_RADIUS_IN_METERS)
            .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)?.run {
            addOnSuccessListener { viewModel.geofenceActivated() }
            addOnFailureListener { Toast.makeText(applicationContext, "DESACTIVADOS", Toast.LENGTH_SHORT).show() }
        }
    }

    private fun removeGeofences() {
        if (!foregroundAndBackgroundLocationPermissionApproved()) {
            return
        }

        geofencingClient.removeGeofences(geofencePendingIntent)?.run {
            addOnSuccessListener { Toast.makeText(applicationContext, ".", Toast.LENGTH_SHORT).show() }
            addOnFailureListener { Toast.makeText(applicationContext, "Err", Toast.LENGTH_SHORT).show() }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        result
            ?.let {
                result.contents
                    ?.let {
                        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                        userPrefs.save("url", it)
                        navController.navigate(R.id.navigation_qr)
                    }
                    ?:run {
                        navController.navigate(R.id.navigation_home)
                    }
            }
            ?:run {
                super.onActivityResult(requestCode, resultCode, data)
            }

    }

}