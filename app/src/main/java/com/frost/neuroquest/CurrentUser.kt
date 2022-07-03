package com.frost.neuroquest

import android.app.Application
import com.frost.neuroquest.model.Places
import com.google.android.gms.maps.model.LatLng

class CurrentUser: Application(){

    companion object{

        var nombre: String?=null
        var email: String?=null
        var puntos = ArrayList<Int>()
        var lugares = ArrayList<Places>()
        var latLngList = ArrayList<LatLng>()

        fun saveCurrentUser(name: String, mail: String){
            this.nombre = name
            this.email = mail
        }

        fun generateLatLongList() {
            lugares.forEach {
                val latLng = LatLng(it.latitude, it.longitude)
                this.latLngList.add(latLng)
            }
        }
    }


}