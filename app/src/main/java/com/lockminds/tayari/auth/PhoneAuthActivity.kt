package com.lockminds.tayari.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.reflect.TypeToken
import com.lockminds.tayari.MainActivity
import com.lockminds.tayari.SessionManager
import com.lockminds.tayari.Tools
import com.lockminds.tayari.constants.APIURLs
import com.lockminds.tayari.constants.Constants
import com.lockminds.tayari.databinding.ActivityPhoneAuthBinding
import com.lockminds.tayari.responses.LoginResponse
import java.util.*
import java.util.concurrent.TimeUnit


class PhoneAuthActivity : AppCompatActivity() {

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private lateinit var binding: ActivityPhoneAuthBinding
    private val tools = Tools()
    private var countDownTimer: CountDownTimer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPhoneAuthBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        tools.setSystemBarLight(this)
        tools.setNavigationBarColor(this)

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = Firebase.auth
        // [END initialize_auth]

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted phone :$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.d(TAG, "onVerificationFailed: "+ e.message);
                Toast.makeText(this@PhoneAuthActivity, "onCodeSent failed :${e.message}", Toast.LENGTH_LONG).show()
                binding.auth.isVisible = true
                binding.overlay.isVisible = false
                binding.sendCode.isVisible = true;
                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(this@PhoneAuthActivity, "code is invalid", Toast.LENGTH_LONG).show()
                    binding.verifyCode.isVisible = true
                    binding.overlay.isVisible = false
                    Log.d(TAG, "onVerificationFailed: "+ e.message);
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(this@PhoneAuthActivity, "failed try another method", Toast.LENGTH_LONG).show()
                    binding.auth.isVisible = true
                    binding.overlay.isVisible = false
                    Log.d(TAG, "onVerificationFailed: "+ e.message);
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                binding.sendCodeContainer.isVisible = false
                binding.verifyCodeContainer.isVisible = true
                binding.overlay.isVisible = false

                countDownTimer()
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
            }
        }
        // [END phone_auth_callbacks]

        initController()
    }

    private fun initController(){

        binding.sendCode.setOnClickListener {
                val phoneNumber = binding.prefix.text.toString() + binding.phoneNumber.text.toString()
            if(phoneNumber.isEmpty()){
                return@setOnClickListener
            }
            startPhoneNumberVerification(phoneNumber)
            it.isVisible = false
            binding.overlay.isVisible = true
        }

        binding.verifyCode.setOnClickListener {
            val code = binding.otpView.text.toString()
            if(code.isEmpty()){
                return@setOnClickListener
            }
            verifyPhoneNumberWithCode(storedVerificationId,code)
            it.isVisible =  false
            binding.overlay.isVisible = true
        }

        binding.resendCode.setOnClickListener {
            val phoneNumber = binding.prefix.text.toString() + binding.phoneNumber.text.toString()
            if(phoneNumber.isEmpty()){
                return@setOnClickListener
            }
            resendVerificationCode(phoneNumber,resendToken)
            it.isVisible = false
            binding.overlay.isVisible = true
        }

        binding.auth.setOnClickListener {
            val intent = Intent(this@PhoneAuthActivity, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser !== null){
            val intent = Intent(this@PhoneAuthActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    // [END on_start_check_user]

    private fun startPhoneNumberVerification(phoneNumber: String) {
        // [START start_phone_auth]
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(120L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        // [END start_phone_auth]
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        // [END verify_with_code]

        signInWithPhoneAuthCredential(credential)
    }

    // [START resend_verification]
    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        binding.verifyCode.isVisible = true
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(120L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    loginServer()
                } else {
                    binding.overlay.isVisible = false
                    // Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        binding.verifyCode.isVisible = true
                        Toast.makeText(this@PhoneAuthActivity, "Verifying code is invalid", Toast.LENGTH_LONG).show()
                        binding.overlay.isVisible = false
                        Log.d(TAG, "signInWithPhoneAuthCredential: failed")
                    }
                    // Update UI
                }
            }
    }
    // [END sign_in_with_phone]

    private fun countDownTimer() {
        countDownTimer = object : CountDownTimer(1000 * 60 * 2, 1000) {
            override fun onTick(l: Long) {
                val text = String.format(
                    Locale.getDefault(), "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(l) % 60,
                    TimeUnit.MILLISECONDS.toSeconds(l) % 60
                )
                binding.tvCoundown.text = text
            }

            override fun onFinish() {
                binding.verifyCode.isVisible = false
                binding.tvCoundown.text = "00:00"
                binding.resendCode.isVisible = true
            }
        }
        (countDownTimer as CountDownTimer).start()
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
                                    binding.overlay.isVisible = false
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
                                    Log.d(TAG, "onResponse: ${response.message}")
                                    Toast.makeText(this@PhoneAuthActivity,  response.message, Toast.LENGTH_LONG).show()

                                    val intent = Intent(this@PhoneAuthActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()

                                } else {
                                    Log.d(TAG, "onResponse: ${response.message}")
                                    binding.overlay.isVisible = false
                                    Toast.makeText(this@PhoneAuthActivity,  response.message, Toast.LENGTH_LONG).show()
                                    signOut()
                                }

                            }

                            override fun onError(anError: ANError) {
                                binding.overlay.isVisible = false
                                Log.d(TAG, "onError: ${anError.errorBody}")
                                Toast.makeText(this@PhoneAuthActivity, anError.errorBody, Toast.LENGTH_SHORT).show()
                                signOut()
                            }

                        })
            } else {
                binding.overlay.isVisible = false
                Log.d(TAG, "loginServer: login fail")
                Toast.makeText(this@PhoneAuthActivity,"Login failed", Toast.LENGTH_SHORT).show()
                signOut()
            }
        }

    }

    private fun signOut() {
        // [START auth_sign_out]
        Firebase.auth.signOut()
        // [END auth_sign_out]

    }

    companion object {
        private const val TAG = "KELLY"
    }

}