package com.lockminds.tayari.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.ParsedRequestListener
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.reflect.TypeToken
import com.lockminds.tayari.MainActivity
import com.lockminds.tayari.R
import com.lockminds.tayari.SessionManager
import com.lockminds.tayari.Tools
import com.lockminds.tayari.constants.APIURLs
import com.lockminds.tayari.constants.Constants
import com.lockminds.tayari.databinding.ActivityAuthBinding
import com.lockminds.tayari.databinding.AddUserDataBinding
import com.lockminds.tayari.firebase.ui.auth.AuthUiActivity
import com.lockminds.tayari.responses.LoginResponse
import com.lockminds.tayari.responses.UserVerifyResponse
import org.json.JSONObject

open class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    private lateinit var buttonFacebookLogin: LoginButton
    private lateinit var alertDialog: AlertDialog;
    private val tools = Tools()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        val view:View = binding.root
        setContentView(view)
        binding.policy.text = tools.fromHtml("By continuing you are indicating that you accept our <a href='https://tayari.co.tz/policy'>Terms of Service</a> and <a href='https://tayari.co.tz/policy'>Privacy policy</a>")
        binding.policy.movementMethod= LinkMovementMethod()
        tools.setSystemBarLight(this)
        tools.setNavigationBarColor(this)
        initComponents()
        initSigning()
    }


    private fun initComponents(){
        binding.phone.isVisible = false
        binding.email.isVisible = false
        binding.google.setOnClickListener {
            Log.e("Kelly","google signin")
            signInGoogle()
            //addData()
        }

        binding.phone.setOnClickListener {
            val intent = Intent(this@AuthActivity, PhoneAuthActivity::class.java)
            startActivity(intent)
        }
        binding.email.setOnClickListener {
            val intent = Intent(this@AuthActivity, EmailPasswordActivity::class.java)
            startActivity(intent)
        }
//        binding.facebook.setOnClickListener {
//            val intent = Intent(this@AuthActivity, FacebookLoginActivity::class.java)
//            startActivity(intent)
//        }
    }

    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser !== null){
            val intent = Intent(this@AuthActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    // [END on_start_check_user]


    private fun initSigning() {

        // [START initialize_fblogin]
        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()

        buttonFacebookLogin = binding.facebook as LoginButton
        buttonFacebookLogin.setReadPermissions("email", "public_profile")
        buttonFacebookLogin.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
            }
        })
        // [END initialize_fblogin]

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

    // [START onactivityresult]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                    Toast.makeText(applicationContext, "Failed to login", Toast.LENGTH_LONG).show()
                Log.w(TAG, "Google sign in failed", e)
            }
        }else{
            // Pass the activity result back to the Facebook SDK
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }

    }
    // [END onactivityresult]

    private fun addData(){
        val builder = AlertDialog.Builder(this@AuthActivity)
        val  viewBinding = AddUserDataBinding.inflate(layoutInflater)
        builder.setView(viewBinding.root)
        viewBinding.buttonContinue.setOnClickListener {
            val phone: String = viewBinding.phoneNumber.text.toString()
            if (phone.isNotEmpty()){
                loginServer(phone)
            }else{
                viewBinding.phoneNumber.error = "Please add phone number"
                viewBinding.phoneNumber.isFocusable = true
            }
        }
        builder.create().show()
    }

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                   // loginServer()
                   // addData()
                    checkUser()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@AuthActivity, task.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }
    // [END auth_with_google]

    // [START signin google]
    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE)
    }
    // [END signin google]

    // [START auth_with_facebook]
    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //loginServer("")
                    checkUser()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
    // [END auth_with_facebook]

    private fun checkUser(){
        binding.overlay.isVisible = true
        val mUser = FirebaseAuth.getInstance().currentUser
        mUser?.getIdToken(true)?.addOnCompleteListener{ task ->
            val idToken: String? = task.result?.token
            AndroidNetworking.get(APIURLs.BASE_URL + "login/userverify/${idToken}")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(
                    object : JSONObjectRequestListener{
                        override fun onResponse(response: JSONObject?) {
                            val status = response?.get("status").toString()
                            Log.d(TAG, "onLoginCheck: $status")
                            if(status == "valid"){
                                loginServer("")
                            }else{
                                addData()
                            }
                        }

                        override fun onError(anError: ANError?) {
                            Log.d(TAG, "onError: ${anError.toString()}")
                        }

                    }
                )
            /*    .getAsParsed(
                                object : TypeToken<UserVerifyResponse?>() {},
                                object : ParsedRequestListener<UserVerifyResponse>{
                                    override fun onResponse(response: UserVerifyResponse?) {
                                        Log.d(TAG, "onLoginCheck: ${response?.status}")
                                        if(response?.status.toString() == "valid"){
                                            loginServer("")
                                        }else{
                                            addData()
                                        }
                                    }

                                    override fun onError(anError: ANError?) {
                                        Log.d(TAG, "onError: ${anError.toString()}")
                                    }

                                }
                            )*/
        }
    }

    @SuppressLint("HardwareIds")
    private fun loginServer(phone: String) {
        binding.overlay.isVisible = true
        val mUser = FirebaseAuth.getInstance().currentUser

        var email: String? = if(mUser?.email.isNullOrBlank()){
            ""
        }else{
            mUser?.email.toString()
        }

        var name: String? = if(mUser?.displayName.isNullOrBlank()){
            ""
        }else{
            mUser?.displayName.toString()
        }

        var phoneNumber: String? = if(mUser?.phoneNumber.isNullOrBlank()){
            phone
        }else{
            mUser?.phoneNumber.toString()
        }

        if (mUser != null) {
            mUser.getIdToken(true)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val idToken: String? = task.result?.token
                        val session = SessionManager(applicationContext)
                        val deviceToken = Settings.Secure.getString(applicationContext?.contentResolver, Settings.Secure.ANDROID_ID)
                        AndroidNetworking.post(APIURLs.BASE_URL + "login")
                            .addBodyParameter("device_name", deviceToken)
                            .addBodyParameter("fb_token", idToken.toString())
                            .addBodyParameter("name", name)
                            .addBodyParameter("fcm_token", session.getFCMToken())
                            .addBodyParameter("phone", phoneNumber)
                            .addBodyParameter("email", email)
                            .addHeaders("accept", "application/json")
                            .setPriority(Priority.HIGH)
                            .build()
                            .getAsParsed(
                                object : TypeToken<LoginResponse?>() {},
                                object : ParsedRequestListener<LoginResponse> {

                                    override fun onResponse(response: LoginResponse) {
                                        if (response.status == true) {

                                            val preference = applicationContext?.getSharedPreferences(
                                                Constants.PREFERENCE_KEY,
                                                Context.MODE_PRIVATE
                                            )
                                                ?: return

                                            with(preference.edit()) {
                                                putString(Constants.LOGIN_STATUS, "true")
                                                putString(Constants.LOGIN_TOKEN, response.token)
                                                putString(Constants.NAME, response.name)
                                                putString(Constants.PHONE_NUMBER, response.phone_number)
                                                putString(Constants.PHOTO_URL, response.photo_url)
                                                putString(Constants.USER_ID, response.id)
                                                putString(Constants.EMAIL, response.email)
                                                apply()
                                            }

                                            Toast.makeText(this@AuthActivity,  response.message, Toast.LENGTH_LONG).show()

                                            val intent = Intent(this@AuthActivity, MainActivity::class.java)
                                            startActivity(intent)
                                            finish()

                                        } else {
                                            binding.overlay.isVisible = false
                                            Toast.makeText(this@AuthActivity,  response.message, Toast.LENGTH_LONG).show()
                                            signOut()
                                        }

                                    }

                                    override fun onError(anError: ANError) {
                                        Toast.makeText(this@AuthActivity, anError.errorBody, Toast.LENGTH_SHORT).show()
                                        signOut()
                                        binding.overlay.isVisible = false
                                    }

                                })
                    } else {
                        Toast.makeText(this@AuthActivity, "Failed to login", Toast.LENGTH_LONG).show()
                        signOut()
                        binding.overlay.isVisible = false
                    }
                }
        }


    }

    private fun signOut() {
        // [START auth_sign_out]
        Firebase.auth.signOut()
        // [END auth_sign_out]

        googleSignInClient.signOut()
            .addOnCompleteListener(this, OnCompleteListener<Void?> {

            })

    }


    companion object {
        private const val TAG = "Kelly"
        private const val RC_SIGN_IN_GOOGLE = 9001
    }

}