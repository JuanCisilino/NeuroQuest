package com.frost.neuroquest

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.frost.neuroquest.databinding.ActivityMainBinding
import com.frost.neuroquest.model.Places
import com.frost.neuroquest.ui.mapa.DashboardViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel : DashboardViewModel
    private val firebaseRemoteConfig = Firebase.remoteConfig
    private val gson = GsonBuilder().create()
    private val userPrefs = UserPrefs(this)

    companion object{
        fun start(context: Context){
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        setFirebaseRemoteConfig()
        if (hasLocationPermission(this)) requestPermission()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_dashboard)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun setFirebaseRemoteConfig() {
        setMinimalInterval()
        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            val exception = task.exception?.message
            when {
                task.isSuccessful -> getRemoteConfig()
                exception != null -> showAlert()
            }
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
        CurrentUser.lugares = gson.fromJson(lugares, object : TypeToken<List<Places>>() {}.type)
        CurrentUser.puntos = getPuntos()
        CurrentUser.generateLatLongList()
    }

    private fun getPuntos(): ArrayList<Int> {
        val puntos = userPrefs.getString("Puntos")
        return if (puntos != null && puntos.isNotEmpty()){
            trimPuntos(puntos) } else { ArrayList() }
    }

    private fun trimPuntos(s: String):ArrayList<Int> {
        TODO("Recortar s , " +
                "dividirlo en una lista de numeros " +
                "y despues manejarlo con los ids de la lista")
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        requestPermission()
    }


}