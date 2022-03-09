package com.user.tayari


import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.reflect.TypeToken
import com.user.tayari.auth.AuthActivity
import com.user.tayari.constants.APIURLs
import com.user.tayari.constants.Constants
import com.user.tayari.datasource.AppDatabase
import com.user.tayari.firebase.ui.auth.AuthUiActivity
import com.user.tayari.responses.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

open class BaseActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    private lateinit var googleSignInClient: GoogleSignInClient

    protected val tools = Tools()

    protected lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(applicationContext)
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

    override fun onResume() {
        super.onResume()
        update_fcm_token()
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

    private fun isNightMode(): Boolean {
        return when (resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
    }

    fun initStatusBar(){
        if(!isNightMode()){
            tools.setSystemBarLight(this)
        }
        tools.setSystemBarColor(this, R.color.colorPrimaryDark)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java).apply { }
                startActivity(intent)
                true
            }

            R.id.logout -> {
                Firebase.auth.signOut()
                GlobalScope.launch {
                    AppDatabase.getDatabase(applicationContext,this).clearAllTables()
                }
                val intent = Intent(this, AuthUiActivity::class.java).apply { }
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private var progressBar: ProgressBar? = null

    fun hideProgressBar() {
        progressBar?.visibility = View.INVISIBLE
    }

    fun update_fcm_token(){
        AndroidNetworking.post(APIURLs.BASE_URL + "user/update_fcm_token")
            .addBodyParameter("fcm_token", sessionManager.getFCMToken())
            .addHeaders("accept", "application/json")
            .setPriority(Priority.HIGH)
            .addHeaders("Authorization", "Bearer " + sessionManager.fetchAuthToken())
            .build()
            .getAsParsed(
                object : TypeToken<Response?>() {},
                object : ParsedRequestListener<Response> {

                    override fun onResponse(response: Response) {
                        if (response.status) {

                        } else {

                        }

                    }

                    override fun onError(anError: ANError) { }

                })
    }


     fun toast(message: String){
        Toast.makeText(this@BaseActivity, message, Toast.LENGTH_SHORT).show()
    }

}

