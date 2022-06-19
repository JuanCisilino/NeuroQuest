package com.frost.neuroquest.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import com.frost.neuroquest.R
import com.frost.neuroquest.UserPrefs
import com.frost.neuroquest.model.User


class LoginViewModel: ViewModel() {

    lateinit var userPrefs: UserPrefs
    lateinit var contextApp: Context

    fun setUserPrefs(context: Context) {
        contextApp = context
        userPrefs = UserPrefs(context)
    }

    fun save(user: User) {
        userPrefs.save(contextApp.getString(R.string.shared_pref_email), user.email)
        userPrefs.save(contextApp.getString(R.string.shared_pref_name), user.nombre)
        userPrefs.save(contextApp.getString(R.string.shared_pref_points), user.puntos)
    }
}