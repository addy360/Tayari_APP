package com.lockminds.tayari.firebase.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.util.ExtraConstants
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.reflect.TypeToken
import com.lockminds.tayari.MainActivity
import com.lockminds.tayari.R
import com.lockminds.tayari.constants.APIURLs
import com.lockminds.tayari.constants.Constants.Companion.EMAIL
import com.lockminds.tayari.constants.Constants.Companion.LOGIN_STATUS
import com.lockminds.tayari.constants.Constants.Companion.LOGIN_TOKEN
import com.lockminds.tayari.constants.Constants.Companion.NAME
import com.lockminds.tayari.constants.Constants.Companion.PHONE_NUMBER
import com.lockminds.tayari.constants.Constants.Companion.PHOTO_URL
import com.lockminds.tayari.constants.Constants.Companion.PREFERENCE_KEY
import com.lockminds.tayari.constants.Constants.Companion.USER_ID
import com.lockminds.tayari.databinding.ActivitySignedBinding
import com.lockminds.tayari.responses.LoginResponse

class SignedActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignedBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        initComponents()
    }


    fun initComponents(){
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
        user.getIdToken(true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken = task.result.token
                    Log.e("KELLY", idToken!!)
                    if (response != null) {
                        if (response.isNewUser) {
                            createUser()
                        } else {
                            loginUser(idToken,response)
                        }
                    }
                    // Return user to login page because we didnt receive response
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
    private fun loginUser(fb_token: String, response: IdpResponse) {
            val deviceToken = Settings.Secure.getString(applicationContext?.contentResolver,Settings.Secure.ANDROID_ID)
            AndroidNetworking.post(APIURLs.BASE_URL + "login/token")
                .addBodyParameter("fb_token", fb_token)
                .addBodyParameter("device_name", deviceToken)
                .addHeaders("accept", "application/json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsParsed(
                    object : TypeToken<LoginResponse?>() {},
                    object : ParsedRequestListener<LoginResponse> {

                        override fun onResponse(response: LoginResponse) {
                            binding.spinKit.visibility = View.GONE
                            if (response.getStatus()) {

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

                                val mySnackbar = Snackbar.make(
                                    binding.lytParent,
                                    getString(R.string.success),
                                    Snackbar.LENGTH_SHORT
                                )
                                mySnackbar.show()

                                val intent = Intent(this@SignedActivity, MainActivity::class.java)
                                startActivity(intent)

                            } else {
                                val mySnackbar = Snackbar.make(
                                    binding.lytParent,
                                    response.message,
                                    Snackbar.LENGTH_LONG
                                )
                                mySnackbar.show()
                            }

                        }

                        override fun onError(anError: ANError) {
                            binding.spinKit.visibility = View.GONE
                            val mySnackbar = Snackbar.make(
                                binding.lytParent,
                                anError.errorDetail,
                                Snackbar.LENGTH_SHORT
                            )
                            mySnackbar.show()
                        }

                    })

    }

    private fun createUser() {}

    companion object {
        @JvmStatic
        fun createIntent(context: Context, response: IdpResponse?): Intent {
            return Intent().setClass(context, SignedActivity::class.java)
                .putExtra(ExtraConstants.IDP_RESPONSE, response)
        }
    }

}