package com.frost.neuroquest.ui.mapa

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.frost.neuroquest.CurrentUser
import com.frost.neuroquest.databinding.FragmentDashboardBinding
import com.frost.neuroquest.hasPermission
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions


class DashboardFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var googleMap: GoogleMap
    private lateinit var viewModel: DashboardViewModel
    private var isZoomed = false

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
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

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        if (hasPermission(requireContext())) googleMap.isMyLocationEnabled = true
        createAndShowMarkers()
    }

    private fun createAndShowMarkers() {
        val builder = LatLngBounds.Builder()
        CurrentUser.latLngList.forEach {
            builder.include(it)
            googleMap.addMarker(MarkerOptions().position(it))
        }
        val bounds = builder.build()
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20))
        googleMap.setOnMarkerClickListener {
            isZoomed = if (isZoomed) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20))
                false
            } else {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it.position, 16F))
                true
            }
            true
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