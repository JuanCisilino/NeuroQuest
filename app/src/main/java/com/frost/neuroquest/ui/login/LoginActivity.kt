package com.frost.neuroquest.ui.login

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.ViewModelProvider
import com.frost.neuroquest.MainActivity
import com.frost.neuroquest.R
import com.frost.neuroquest.databinding.ActivityLoginBinding
import com.frost.neuroquest.ui.LoadingDialog

class LoginActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this)[LoginViewModel::class.java] }
    private lateinit var binding: ActivityLoginBinding
    private val GOOGLE_SIGN_IN = 100
    private var loadingDialog = LoadingDialog(R.string.loading_message)

    companion object{
        fun start(activity: Activity){
            activity.startActivity(Intent(activity.baseContext, LoginActivity::class.java))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        splash()
        super.onCreate(savedInstanceState)
        setBinding()
        setBtns()
    }

    private fun splash() {
        Thread.sleep(2000)
        setTheme(R.style.Theme_NeuroQuest)
    }

    private fun checkSession() {

    }

    private fun setBtns() {
        binding.btn.setOnClickListener {
            loadingDialog.show(supportFragmentManager)
            MainActivity.start(this)
        }
        binding.googleButton.setOnClickListener { startGoogle() }
        checkSession()
    }

    private fun startGoogle() {
//        val googleConfig = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken("1077617626551-97u78q84mv36j79a8ebjmng4e1d1peo3.apps.googleusercontent.com")
//            .requestEmail()
//            .build()
//        val googleClient = GoogleSignIn.getClient(this, googleConfig)
//        googleClient.signOut()
//        startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
    }

    private fun setBinding(){
        viewModel.setUserPrefs(this)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onBackPressed() { loadingDialog.dismiss() }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == GOOGLE_SIGN_IN){
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try{
//                val account = task.getResult(ApiException::class.java)
//                account?.let {
//                    signInWithCredential(GoogleAuthProvider.getCredential(it.idToken, null))
//                        .addOnCompleteListener {
//                            loadingDialog.show(supportFragmentManager)
//                            account.email?.let { viewModel.getUserByEmail(it) }
//                            Handler().postDelayed(Runnable {
//                                if (it.isSuccessful){
//                                    account.email?.let { validateAndContinue(account) }
//                                }else {
//                                    showAlert()
//                                }
//                            }, 2000)
//                        }
//                }
//            }catch (e: ApiException){
//                showAlert()
//            }
//        }
    }

//    private fun validateAndContinue(account: GoogleSignInAccount) {
//        val newUser = User(
//            email = account.email?:"",
//            nombre = account.displayName?:"none",
//            puntos = aca va el punto actual en cuanto el usuario se loguea
//        )
//        viewModel.user?.let { handleUser(it) }
//            ?:run { viewModel.saveUser(newUser) }
//
//    }
}