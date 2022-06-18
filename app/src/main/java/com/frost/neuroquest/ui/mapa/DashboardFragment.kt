package com.frost.neuroquest.ui.mapa

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.frost.neuroquest.R
import com.frost.neuroquest.databinding.FragmentDashboardBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.lang.Exception

class DashboardFragment : Fragment(), OnMapReadyCallback, EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var googleMap: GoogleMap

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.map.onCreate(savedInstanceState)
        binding.map.getMapAsync(this)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        googleMap.clear()
        _binding = null
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        checkPermission()
        googleMap.isMyLocationEnabled = true
        val latLng = getLocationFromAddress()
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16F))
        googleMap.addMarker(MarkerOptions().position(latLng))
    }

    private fun checkPermission(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(this, getString(R.string.allow), 0,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.allow), 0,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) { }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms))
            AppSettingsDialog.Builder(this).build().show() else checkPermission()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun getLocationFromAddress(): LatLng {
        var pointResult = LatLng(-34.650333, -58.487032)

        try {
            val addressList = ArrayList<LatLng>()
            if (addressList.isNotEmpty()) {
                val location = addressList[0]
                pointResult = LatLng(location.latitude, location.longitude)
            }
        } catch (ex: Exception) {
            // TODO: fire event in Firebase or similar
            ex.printStackTrace()
        } finally {
            return pointResult
        }
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.map.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.map.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.map.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.map.onSaveInstanceState(outState)
    }
}