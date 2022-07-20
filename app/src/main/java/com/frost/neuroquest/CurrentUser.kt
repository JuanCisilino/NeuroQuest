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
        var disponibles = ArrayList<Places>()
        var latLngList = ArrayList<Pair<LatLng, String>>()
        var persSaved = true

        fun saveCurrentUser(name: String, mail: String, punto: String){
            this.nombre = name
            this.email = mail
            this.puntos = split(punto)
        }

        private fun split(puntos: String): ArrayList<Int> {
            if (puntos == "") return ArrayList()
            val lista = ArrayList<Int>()
            listOf(*puntos.split(",").toTypedArray()).forEach { lista.add(it.toInt()) }
            return lista
        }

        fun generateLatLongList() {
            lugares.forEach {
                val latLng = LatLng(it.latitude, it.longitude)
                this.latLngList.add(Pair(latLng, it.nombre))
            }
        }
    }


}