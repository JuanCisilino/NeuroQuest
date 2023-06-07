package com.frost.neuroquest.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import com.frost.neuroquest.CurrentUser
import com.frost.neuroquest.R
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
        CurrentUser.disponibles.forEach { handle(it) }
        CurrentUser.persSaved = true
    }

    fun containsOrAdd(lugares: List<Places>){
        if (CurrentUser.disponibles.isEmpty()) return
        lugares.forEach {
            when {
                CurrentUser.puntos.contains(it.id) -> return
                CurrentUser.split(userPrefs.getString(contextApp.getString(R.string.shared_pref_points))?:"").isNotEmpty() -> prepareCharactersAndPlaces()
                else -> save = true
            }
        }
        if (save) savePlaces(lugares)
    }

    private fun savePlaces(lugares: List<Places>) {
        save = false
        val puntos = ArrayList<Int>()
        lugares.forEach { puntos.add(it.id) }
        userPrefs.save(contextApp.getString(R.string.shared_pref_points), puntos.joinToString(","))
    }

    fun prepareCharactersAndPlaces() {
        val puntos = CurrentUser.split(userPrefs.getString(contextApp.getString(R.string.shared_pref_points))?:"")
        puntos.forEach { punto ->
            val lugar = CurrentUser.lugares.find { it.id == punto }
            lugar?.let {
                handle(it)
                CurrentUser.disponibles.add(it) }
        }
    }

    private fun handle(lugar: Places) {
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
}