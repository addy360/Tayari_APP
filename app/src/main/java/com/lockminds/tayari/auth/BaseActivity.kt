package com.lockminds.tayari.auth

import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lockminds.tayari.R
import com.lockminds.tayari.Tools


abstract class BaseActivity : AppCompatActivity() {

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    private lateinit var googleSignInClient: GoogleSignInClient

    protected val tools = Tools()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tools.setSystemBarLight(this)
        tools.setNavigationBarColor(this)
        initSigning()
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        setContentView(layoutResID)
    }

    override fun onStart() {
        super.onStart()
        loginStatus()
    }

    private fun loginStatus(){
        val auth = Firebase.auth
        if(auth.currentUser == null){
            val intent = Intent(this@BaseActivity, AuthActivity::class.java)
            startActivity(intent)
        }
    }

    protected fun signOut() {
        // [START auth_sign_out]
        Firebase.auth.signOut()
        // [END auth_sign_out]

        googleSignInClient.signOut()
            .addOnCompleteListener(this, OnCompleteListener<Void?> {
                val intent = Intent(this@BaseActivity, AuthActivity::class.java)
                startActivity(intent)
                finish()
            })

    }

    private fun initSigning() {
        // [START config_signin]
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        // [END config_signin]

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = Firebase.auth
        // [END initialize_auth]
    }

    companion object {
        private var sTaskDescription: ActivityManager.TaskDescription? = null
    }

}