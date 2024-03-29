package com.frost.neuroquest.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.frost.neuroquest.*
import com.frost.neuroquest.databinding.ActivityLoginBinding
import com.frost.neuroquest.helpers.logEventAnalytics
import com.frost.neuroquest.helpers.showAlert
import com.frost.neuroquest.helpers.signInWithCredential
import com.frost.neuroquest.model.User
import com.frost.neuroquest.ui.LoadingDialog
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider


@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this)[LoginViewModel::class.java] }
    private lateinit var binding: ActivityLoginBinding

    private val GOOGLE_SIGN_IN = 100
    private var loadingDialog = LoadingDialog(R.string.loading_message)

    override fun onCreate(savedInstanceState: Bundle?) {
        splash()
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)
        setBinding()
        setBtns()
    }

    private fun splash() {
        Thread.sleep(2000)
        setTheme(R.style.Theme_NeuroQuest)
    }

    private fun checkSession() {
        val usuario = viewModel.getUser()
        if (!usuario?.nombre.isNullOrEmpty()) {
            logEventAnalytics("Ingreso", usuario!!.email)
//            CurrentUser.saveCurrentUser(usuario.nombre, usuario.email)
            goToMainActivity()
        }
    }

    private fun setBtns() {
        binding.btn.setOnClickListener {
            loadingDialog.show(supportFragmentManager)
            validateAndContinue()
        }
        binding.googleButton.setOnClickListener { startGoogle() }
//        checkSession()
    }

    private fun goToMainActivity(){
        MainActivity.start(this)
        finish()
    }

    private fun startGoogle() {
        val googleConfig = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("646390398515-fr5evdvra79bppgs4da444lahfgciol4.apps.googleusercontent.com")
            .requestEmail()
            .build()
        val googleClient = GoogleSignIn.getClient(this, googleConfig)
        googleClient.signOut()
        startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
    }

    private fun setBinding(){
        viewModel.setUserPrefs(this)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onBackPressed() { loadingDialog.dismiss() }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN){
            loadingDialog.show(supportFragmentManager)
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = task.getResult(ApiException::class.java)
                account?.let {
                    signInWithCredential(GoogleAuthProvider.getCredential(it.idToken, null))
                        .addOnCompleteListener {
                            if (it.isSuccessful){
                                account.email?.let { validateAndContinue(account) }
                            }else {
                                showAlert()
                            }
                        }
                }
            }catch (e: ApiException){
                showAlert()
            }
        }
    }

    private fun validateAndContinue(account: GoogleSignInAccount?= null) {
        val usuario = viewModel.getUser()
        if (!usuario?.nombre.isNullOrEmpty()) {
            logEventAnalytics("Ingreso", usuario!!.email)
        } else {
            viewModel.save(
                User(email = account?.email?:"",
                    nombre = account?.displayName?:"",
                    puntos = ""))
            logEventAnalytics("Nuevo Usuario", account?.email?:"")
        }
        goToMainActivity()
    }
}