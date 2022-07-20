package com.frost.neuroquest.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.frost.neuroquest.CurrentUser
import com.frost.neuroquest.UserPrefs
import com.frost.neuroquest.model.Places

class HomeViewModel : ViewModel() {

    lateinit var userPrefs: UserPrefs
    lateinit var contextApp: Context
    private var save = false

    fun setUserPrefs(context: Context) {
        contextApp = context
        userPrefs = UserPrefs(context)
    }

    fun setCharacters(){
        if (CurrentUser.persSaved) return
        CurrentUser.disponibles.forEach { lugar ->
            lugar.personjes.forEach { pers ->
                val personaje = Places(
                    id = 0,
                    nombre = pers.nombre,
                    latitude = 0.0,
                    longitude = 0.0,
                    image_url = pers.url,
                    url = pers.url,
                    personjes = ArrayList())
                CurrentUser.disponibles.add(personaje)
            }
        }
        CurrentUser.persSaved = true
    }

    fun containsOrAdd(lugares: List<Places>){
        if (CurrentUser.puntos.isEmpty()) return
        lugares.forEach {
            if (CurrentUser.puntos.contains(it.id)) return
            else save = true
        }
        if (save) savePlaces(lugares)
    }

    private fun savePlaces(lugares: List<Places>) {
        save = false
        val puntos = ArrayList<Int>()
        lugares.forEach { puntos.add(it.id) }
        userPrefs.save("Puntos", puntos.joinToString(","))
        CurrentUser.puntos = puntos
    }
}