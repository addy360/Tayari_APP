package com.lockminds.tayari


import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.lockminds.tayari.firebase.ui.auth.AuthUiActivity
import com.lockminds.tayari.firebase.ui.auth.SignedActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

open class BaseActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: FirebaseUser
    lateinit var authStateListener: FirebaseAuth.AuthStateListener
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var database: DatabaseReference


    private var MyIp: String? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        Tools.setSystemBarLight(this)
        mAuth = Firebase.auth
        mUser = mAuth.currentUser!!
        MyIp = Tools.getLocalIpAddress()
        Tools.NetPolicy()

    }

    override fun onResume() {
        super.onResume()
        firebaseAuth = FirebaseAuth.getInstance()
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                val intent = Intent(this, AuthUiActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        firebaseAuth.addAuthStateListener(this.authStateListener)
    }


    public override fun onStop() {
        super.onStop()
        hideProgressBar()
        firebaseAuth.removeAuthStateListener(this.authStateListener)
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
            Tools.setSystemBarLight(this)
        }
        Tools.setSystemBarColor(this, R.color.colorPrimaryDark)
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



}

