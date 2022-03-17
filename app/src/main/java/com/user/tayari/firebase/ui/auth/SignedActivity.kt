package com.user.tayari.firebase.ui.auth

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.util.ExtraConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.reflect.TypeToken
import com.user.tayari.MainActivity
import com.user.tayari.SessionManager
import com.user.tayari.constants.APIURLs
import com.user.tayari.constants.Constants.Companion.EMAIL
import com.user.tayari.constants.Constants.Companion.LOGIN_STATUS
import com.user.tayari.constants.Constants.Companion.LOGIN_TOKEN
import com.user.tayari.constants.Constants.Companion.NAME
import com.user.tayari.constants.Constants.Companion.PHONE_NUMBER
import com.user.tayari.constants.Constants.Companion.PHOTO_URL
import com.user.tayari.constants.Constants.Companion.PREFERENCE_KEY
import com.user.tayari.constants.Constants.Companion.USER_ID
import com.user.tayari.responses.LoginResponse
import user.tayari.databinding.ActivitySignedBinding

class SignedActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignedBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        initComponents()
    }

    @SuppressLint("RestrictedApi")
    private fun initComponents(){
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            startActivity(AuthUiActivity.createIntent(this))
            finish()
            return
        }
        val response: IdpResponse? = intent.getParcelableExtra(ExtraConstants.IDP_RESPONSE)
        populateProfile(response)
    }

    private fun populateProfile(response: IdpResponse?) {

        val user = FirebaseAuth.getInstance().currentUser

        user?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val idToken = task.result.token.toString()

                val name = user?.displayName
                val phone = user?.phoneNumber
                val email = user?.email

                if (response != null) {
                    if (response.isNewUser) {
                        createUser(idToken,name.toString(),email.toString(),phone.toString())
                    } else {
                        loginUser(idToken)
                    }
                }
                // Return user to login page because we didn't receive response
                val intent = Intent(this@SignedActivity, AuthUiActivity::class.java)
                startActivity(intent)
                finish()
            }

            // Return user to login page because task was not successfully
            val intent = Intent(this@SignedActivity, AuthUiActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @SuppressLint("HardwareIds")
    private fun loginUser(fb_token: String) {
        Log.e("KELLY"," the sfs --------"+fb_token);
            val session = SessionManager(applicationContext)
            val deviceToken = Settings.Secure.getString(applicationContext?.contentResolver,Settings.Secure.ANDROID_ID)
            AndroidNetworking.post(APIURLs.BASE_URL + "login/token")
                .addBodyParameter("fb_token", fb_token)
                .addBodyParameter("fcm_token", session.getFCMToken())
                .addBodyParameter("device_name", deviceToken)
                .addHeaders("accept", "application/json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsParsed(
                    object : TypeToken<LoginResponse?>() {},
                    object : ParsedRequestListener<LoginResponse> {

                        override fun onResponse(response: LoginResponse) {
                            binding.spinKit.visibility = View.GONE
                            if (response.status == true) {

                                val preference = applicationContext?.getSharedPreferences(
                                    PREFERENCE_KEY,
                                    Context.MODE_PRIVATE
                                )
                                    ?: return

                                with(preference.edit()) {
                                    putString(LOGIN_STATUS, "true")
                                    putString(LOGIN_TOKEN, response.token)
                                    putString(NAME, response.name)
                                    putString(PHONE_NUMBER, response.phone_number)
                                    putString(PHOTO_URL, response.photo_url)
                                    putString(USER_ID, response.id)
                                    putString(EMAIL, response.email)
                                    apply()
                                }

                                Toast.makeText(this@SignedActivity,  response.message, Toast.LENGTH_SHORT).show()

                                val intent = Intent(this@SignedActivity, MainActivity::class.java)
                                startActivity(intent)

                            } else {
                                Toast.makeText(this@SignedActivity,  response.message, Toast.LENGTH_SHORT).show()
                                AuthUI.getInstance()
                                        .signOut(this@SignedActivity)
                                        .addOnCompleteListener {
                                            clearAppData()
                                            val intent = Intent(this@SignedActivity, AuthUiActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                            }

                        }

                        override fun onError(anError: ANError) {
                            binding.spinKit.visibility = View.GONE
                            Toast.makeText(this@SignedActivity, anError.errorDetail, Toast.LENGTH_SHORT).show()
                        }

                    })

    }

    @SuppressLint("HardwareIds")
    private fun createUser(fb_token: String, name: String, email: String, phone: String) {
        val session = SessionManager(applicationContext)
        val deviceToken = Settings.Secure.getString(applicationContext?.contentResolver,Settings.Secure.ANDROID_ID)

        AndroidNetworking.post(APIURLs.BASE_URL + "register")
                .addBodyParameter("device_name", deviceToken)
                .addBodyParameter("fb_token", fb_token)
                .addBodyParameter("name", name)
                .addBodyParameter("fcm_token", session.getFCMToken())
                .addBodyParameter("phone", phone)
                .addBodyParameter("email", email)
                .addHeaders("accept", "application/json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsParsed(
                        object : TypeToken<LoginResponse?>() {},
                        object : ParsedRequestListener<LoginResponse> {

                            override fun onResponse(response: LoginResponse) {
                                binding.spinKit.visibility = View.GONE
                                if (response.status) {

                                    val preference = applicationContext?.getSharedPreferences(
                                            PREFERENCE_KEY,
                                            Context.MODE_PRIVATE
                                    )
                                            ?: return

                                    with(preference.edit()) {
                                        putString(LOGIN_STATUS, "true")
                                        putString(LOGIN_TOKEN, response.token)
                                        putString(NAME, response.name)
                                        putString(PHONE_NUMBER, response.phone_number)
                                        putString(PHOTO_URL, response.photo_url)
                                        putString(USER_ID, response.id)
                                        putString(EMAIL, response.email)
                                        apply()
                                    }

                                    Toast.makeText(this@SignedActivity, response.message, Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@SignedActivity, MainActivity::class.java)
                                    startActivity(intent)

                                } else {
                                    Toast.makeText(this@SignedActivity, response.message, Toast.LENGTH_SHORT).show()
                                    val user = FirebaseAuth.getInstance().currentUser
                                    user?.delete()
                                    Firebase.auth.signOut()
                                    clearAppData()
                                }

                            }

                            override fun onError(anError: ANError) {
                                binding.spinKit.visibility = View.GONE
                                Toast.makeText(this@SignedActivity,anError.errorDetail, Toast.LENGTH_SHORT).show()
                            }

                        })
    }

    companion object {
        @SuppressLint("RestrictedApi")
        @JvmStatic
        fun createIntent(context: Context, response: IdpResponse?): Intent {
            return Intent().setClass(context, SignedActivity::class.java)
                .putExtra(ExtraConstants.IDP_RESPONSE, response)
        }
    }

    protected fun clearAppData() {
        try {
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                (getSystemService(ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()
            } else {
                Runtime.getRuntime().exec("pm clear " + applicationContext.packageName)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

}