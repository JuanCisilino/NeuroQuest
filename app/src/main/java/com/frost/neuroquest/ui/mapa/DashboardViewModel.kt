package com.frost.neuroquest.ui.mapa

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.frost.neuroquest.model.Places
import com.google.android.gms.maps.model.LatLng

class DashboardViewModel : ViewModel() {

    var puntos = listOf<Int>()
    var lugares = listOf<Places>()

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text


}