package com.frost.neuroquest.ui.mapa

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.frost.neuroquest.model.Places
import com.google.android.gms.maps.model.LatLng

class DashboardViewModel(state: SavedStateHandle) : ViewModel() {
    private val _geofenceIndex = state.getLiveData(GEOFENCE_INDEX_KEY, -1)
    private val _hintIndex = state.getLiveData(HINT_INDEX_KEY, 0)
    var lugares = listOf<Places>()
    var geofensas = listOf<Places>()

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    fun geofenceIsActive() =_geofenceIndex.value == _hintIndex.value

    fun geofenceActivated() {
        _geofenceIndex.value = _hintIndex.value
    }
}

private const val HINT_INDEX_KEY = "hintIndex"
private const val GEOFENCE_INDEX_KEY = "geofenceIndex"