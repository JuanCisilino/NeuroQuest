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