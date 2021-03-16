package com.lockminds.tayari


import android.content.Intent
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
import com.google.firebase.ktx.Firebase
import com.lockminds.tayari.firebase.ui.auth.AuthUiActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

open class BaseActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: FirebaseUser

    protected var MyIp: String? = null

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
        val auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            val intent = Intent(applicationContext, AuthUiActivity::class.java)
            startActivity(intent)
            finish()
        }
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

            R.id.edit_profile -> {
                val intent = Intent(this, ProfileEditActivity::class.java).apply { }
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

    public override fun onStop() {
        super.onStop()
        hideProgressBar()
    }


}

