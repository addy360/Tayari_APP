package com.user.tayari

import android.Manifest
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.google.gson.reflect.TypeToken
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.user.tayari.constants.APIURLs
import com.user.tayari.constants.Constants
import com.user.tayari.databinding.ActivityProfileBinding
import com.user.tayari.firebase.ui.auth.AuthUiActivity
import com.user.tayari.responses.Response
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


class ProfileActivity : BaseActivity() {

    lateinit var binding: ActivityProfileBinding

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        initStatusBar()
        initComponents()
        initInformation()

        binding.cart.setOnClickListener {

            GlobalScope.launch {

                val cart = (application as App).repository.getOneCart()

                if(cart != null){
                    val restaurant  = (application as App).repository.getRestaurant(cart.team_id.toString())
                    runOnUiThread {
                        startActivity(
                                CartActivity.createCartIntent(
                                        this@ProfileActivity,
                                        restaurant
                                )
                        )
                    }
                }else{
                    showNoCart()
                }

            }


        }

        binding.profile.setOnClickListener {
            Toast.makeText(this@ProfileActivity, "You'r Here", Toast.LENGTH_SHORT).show()
        }

        binding.orders.setOnClickListener {
            val intent = Intent(this@ProfileActivity, OrdersActivity::class.java)
            startActivity(intent)
        }

        binding.home.setOnClickListener {
            val intent = Intent(this@ProfileActivity, MainActivity::class.java)
            startActivity(intent)
        }
        
    }

    private fun showNoCart() {
        runOnUiThread(Runnable {
            Toast.makeText(this@ProfileActivity, "No item", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onResume() {
        super.onResume()
        val preference = applicationContext?.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE)
        if (preference != null) {

            Glide
                    .with(applicationContext)
                    .load(preference.getString(Constants.PHOTO_URL, ""))
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher_round)
                    .into(binding.image)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initComponents() {
        binding.toolbar.navigationIcon = getDrawable(R.drawable.ic_back)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = null
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        binding.logout.setOnClickListener {
            signOut()
        }

        binding.aboutTeam.setOnClickListener {
            showAboutApp()
        }

        binding.privacyPolicy.setOnClickListener {
            loadWebFromUrl()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_close, menu)
        tools.changeMenuIconColor(menu, resources.getColor(R.color.colorPrimaryVariant))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home || item.itemId == R.id.action_close) {
            finish()
        } else {
            Toast.makeText(applicationContext, item.title, Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun initInformation(){
        val preference = applicationContext?.getSharedPreferences(
                Constants.PREFERENCE_KEY,
                Context.MODE_PRIVATE
        )
        if (preference != null) {
            binding.name.text = preference.getString(
                    Constants.NAME,
                    ""
            )
            binding.name1.text = preference.getString(
                    Constants.NAME,
                    ""
            )
            binding.phoneNumber.text = preference.getString(Constants.PHONE_NUMBER, "")
            Glide
                    .with(applicationContext)
                    .load(preference.getString(Constants.PHOTO_URL, ""))
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher_round)
                    .into(binding.image)
            binding.contact.text = preference.getString(Constants.EMAIL, "")


                binding.fab.isVisible = true


            binding.fab.setOnClickListener {
                val intent = Intent(this@ProfileActivity, UpdateProfileActivity::class.java)
                startActivity(intent)
                finish()
            }


            binding.image.setOnClickListener {

                    Dexter.withContext(this)
                            .withPermissions(
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ).withListener(object : MultiplePermissionsListener {

                                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                                    CropImage.activity()
                                            .setGuidelines(CropImageView.Guidelines.ON)
                                            .start(this@ProfileActivity);
                                }

                                override fun onPermissionRationaleShouldBeShown(
                                        permissions: List<PermissionRequest?>?,
                                        token: PermissionToken?
                                ) { /* ... */
                                }
                            }).check()

            }


        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result: CropImage.ActivityResult? = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                binding.spinKit.visibility = View.VISIBLE
                val preference = applicationContext?.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE)
                val resultUri: Uri = result!!.getUri()
                val file: File = File(resultUri.path)
                if (preference != null) {
                    AndroidNetworking.upload(APIURLs.BASE_URL + "user/change_picture")
                            .addMultipartFile("photo", file)
                            .addHeaders("accept", "application/json")
                            .addHeaders("Authorization", "Bearer " + preference.getString(Constants.LOGIN_TOKEN, "false"))
                            .setPriority(Priority.HIGH)
                            .build()
                            .setUploadProgressListener { bytesUploaded, totalBytes ->
                                // do anything with progress
                            }
                            .getAsParsed(
                                    object : TypeToken<Response?>() {},
                                    object : ParsedRequestListener<Response> {

                                        override fun onResponse(response: Response) {
                                            binding.spinKit.visibility = View.GONE
                                            if (response.getStatus()) {

                                                val preference = applicationContext?.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE)
                                                        ?: return

                                                with(preference.edit()) {
                                                    putString(Constants.PHOTO_URL, response.message)
                                                    apply()
                                                }

                                                Glide
                                                        .with(applicationContext)
                                                        .load(response.message)
                                                        .centerCrop()
                                                        .placeholder(R.mipmap.ic_launcher_round)
                                                        .into(binding.image)


                                                val mySnackbar = Snackbar.make(binding.lytParent, getString(R.string.success), Snackbar.LENGTH_SHORT)
                                                mySnackbar.show()

                                            } else {
                                                val mySnackbar = Snackbar.make(binding.lytParent, response.message, Snackbar.LENGTH_LONG)
                                                mySnackbar.show()
                                            }

                                        }

                                        override fun onError(anError: ANError) {
                                            binding.spinKit.visibility = View.GONE
                                            val mySnackbar = Snackbar.make(binding.lytParent, anError.errorDetail, Snackbar.LENGTH_SHORT)
                                            mySnackbar.show()
                                        }

                                    })
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error: Exception = result!!.getError()
            }
        }
    }

    private fun showAboutApp() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.dialog_app_about)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.findViewById<View>(R.id.bt_close).setOnClickListener { dialog.dismiss() }
        val preference = applicationContext?.getSharedPreferences(
                Constants.PREFERENCE_KEY,
                Context.MODE_PRIVATE
        )
        if (preference != null) {
            dialog.findViewById<TextView>(R.id.app_address).text = preference.getString(
                    Constants.TEAM_ADDRESS,
                    "Address"
            )
            dialog.findViewById<TextView>(R.id.app_text).text = preference.getString(
                    Constants.TEAM_NAME, resources.getString(
                    R.string.app_name
            )
            )
            dialog.findViewById<TextView>(R.id.app_phone).text = preference.getString(
                    Constants.TEAM_PHONE,
                    "Telephone"
            )
            dialog.findViewById<TextView>(R.id.app_email).text = preference.getString(
                    Constants.TEAM_EMAIL,
                    "Email"
            )
        }

        dialog.show()
    }

    private fun loadWebFromUrl() {
        val preference = applicationContext?.getSharedPreferences(
                Constants.PREFERENCE_KEY,
                Context.MODE_PRIVATE
        )
        if (preference != null) {
            val url = preference.getString(Constants.POLICY_URL, "null")
            if(url.equals("null"))
                return

            val webpage: Uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }

    }

}