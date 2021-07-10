package com.lockminds.tayari.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.reflect.TypeToken
import com.lockminds.tayari.MainActivity
import com.lockminds.tayari.R
import com.lockminds.tayari.SessionManager
import com.lockminds.tayari.Tools
import com.lockminds.tayari.constants.APIURLs
import com.lockminds.tayari.constants.Constants
import com.lockminds.tayari.databinding.ActivityEmailPasswordBinding
import com.lockminds.tayari.responses.LoginResponse

class EmailPasswordActivity : AppCompatActivity(), TextWatcher {
    private lateinit var binding: ActivityEmailPasswordBinding
    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]
    private val tools = Tools()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailPasswordBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        tools.setSystemBarLight(this)
        tools.setNavigationBarColor(this)

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = Firebase.auth
        // [END initialize_auth]

        initControllers()
    }

    private fun initControllers() {
        binding.loginBtn.setOnClickListener {
                if (buttonState()){
                    binding.overlay.isVisible = true
                    val email = binding.email.text.toString()
                    val password = binding.password.text.toString()
                    createAccount(email,password)
                }else{
                    Toast.makeText(this@EmailPasswordActivity, "Check details", Toast.LENGTH_SHORT).show()
                }
        }

        binding.password.addTextChangedListener(this)
    }

    override fun afterTextChanged(s: Editable) {}
    override fun beforeTextChanged(
        s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        updatePasswordStrengthView(s.toString())
    }

    private fun buttonState(): Boolean{
        val email = binding.email.text.toString()
        val strength = binding.passwordStrength.text.toString()
        val validEmail = tools.isValidEmail(email)
        return validEmail && strength != "Week"
    }

    private fun updatePasswordStrengthView(password: String) {

        val progressBar = findViewById<ProgressBar>(R.id.progressBar) as ProgressBar
        val strengthView = findViewById<ProgressBar>(R.id.password_strength) as TextView
        if (TextView.VISIBLE != strengthView.visibility)
            return

        if (TextUtils.isEmpty(password)) {
            strengthView.text = ""
            progressBar.progress = 0
            return
        }

        val str = PasswordStrength.calculateStrength(password)
        strengthView.text = str.getText(this)
        strengthView.setTextColor(str.color)

        progressBar.progressDrawable.setColorFilter(str.color, android.graphics.PorterDuff.Mode.SRC_IN)
        if (str.getText(this) == "Weak") {
            progressBar.progress = 25
        } else if (str.getText(this) == "Medium") {
            progressBar.progress = 50
        } else if (str.getText(this) == "Strong") {
            progressBar.progress = 75
        } else {
            progressBar.progress = 100
        }
    }

    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser !== null){
            val intent = Intent(this@EmailPasswordActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    // [END on_start_check_user]

    private fun createAccount(email: String, password: String) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    loginServer()
                } else {
                    signIn(email,password)
                }
            }
        // [END create_user_with_email]
    }

    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    loginServer()
                } else {
                    binding.overlay.isVisible = false
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        // [END sign_in_with_email]
    }

    private fun sendEmailVerification() {
        // [START send_email_verification]
        val user = auth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                // Email Verification sent
            }
        // [END send_email_verification]
    }

    @SuppressLint("HardwareIds")
    private fun loginServer() {
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
            ""
        }else{
            mUser?.phoneNumber.toString()
        }

        mUser?.getIdToken(true)?.addOnCompleteListener { task ->
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
                                binding.overlay.isVisible = false
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

                                    Toast.makeText(this@EmailPasswordActivity,  response.message, Toast.LENGTH_LONG).show()

                                    val intent = Intent(this@EmailPasswordActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()

                                } else {
                                    Toast.makeText(this@EmailPasswordActivity,  response.message, Toast.LENGTH_LONG).show()
                                    binding.overlay.isVisible = false
                                    signOut()
                                }

                            }

                            override fun onError(anError: ANError) {
                                Toast.makeText(this@EmailPasswordActivity, anError.errorBody, Toast.LENGTH_SHORT).show()
                                binding.overlay.isVisible = false
                                signOut()
                            }

                        })
            } else {
                Toast.makeText(this@EmailPasswordActivity,"Login failed", Toast.LENGTH_SHORT).show()
                binding.overlay.isVisible = false
                signOut()
            }
        }

    }

    private fun signOut() {
        // [START auth_sign_out]
        Firebase.auth.signOut()
        // [END auth_sign_out]

    }

}