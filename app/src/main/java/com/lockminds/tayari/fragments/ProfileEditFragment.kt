package com.lockminds.tayari.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.lockminds.tayari.App
import com.lockminds.tayari.MainActivity
import com.lockminds.tayari.R
import com.lockminds.tayari.Tools
import com.lockminds.tayari.databinding.FragmentProfileEditBinding
import com.lockminds.tayari.datasource.tables.Users
import com.lockminds.tayari.responses.LoginResponse
import com.lockminds.tayari.viewModels.UsersViewModel
import com.lockminds.tayari.viewModels.UsersViewModelFactory
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.reflect.TypeToken
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.lockminds.tayari.constants.APIURLs
import com.lockminds.tayari.constants.Constants.Companion.PREFERENCE_KEY
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileEditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileEditFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val userViewModel: UsersViewModel by viewModels {
        UsersViewModelFactory((requireActivity().application as App).repository)
    }

    private var gender: String? = null
    private var _binding: FragmentProfileEditBinding? = null

    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.editProfileToolbar.inflateMenu(R.menu.edit_profile_menu)
        binding.editProfileToolbar.setNavigationIcon(R.drawable.ic_back)

        binding.editProfileToolbar.setNavigationOnClickListener { view ->
            requireActivity().onBackPressed()
        }

        binding.editProfileToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_done -> {
                    attempt_update()
                    true
                }
                else -> false
            }
        }

        userViewModel.user.observe(viewLifecycleOwner, Observer { user ->

            user?.let {
                binding.name.setText(user.name)
            }
        })

        pictureUpdate()

    }


    fun pictureUpdate(){
        binding.profilePicture.setOnClickListener { view ->
            Dexter.withContext(activity).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(response: PermissionGrantedResponse) {
                            val mimeType = "image/png"
                            val intent = Intent(Intent.ACTION_GET_CONTENT)
                            intent.type = mimeType
                            val mimeTypes = arrayOf("image/jpeg", "image/jpg", "image/bmp")
                            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                            intent.addCategory(Intent.CATEGORY_OPENABLE)
                            val chooserIntent: Intent
                            chooserIntent = Intent.createChooser(intent, "Choose Image")
                            try {
                                val uri = chooserIntent.data
                                CropImage.activity(uri)
                                        .start(requireActivity())
                            } catch (ex: ActivityNotFoundException) {
                                Toast.makeText(activity, "No suitable File Manager was found.", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onPermissionDenied(response: PermissionDeniedResponse) {
                            if (response.isPermanentlyDenied) {
                                val builder = AlertDialog.Builder(requireActivity())
                                builder.setTitle("Permission Denied").setMessage("Permission to read external storage is permanently denied. you need to go to settings to allow the permission").setNegativeButton("Cancel", null).setPositiveButton("OK") { dialog: DialogInterface?, which: Int ->
                                    val intent = Intent()
                                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                    intent.data = Uri.fromParts("package", requireActivity().packageName, null)
                                    startActivityForResult(intent, 55)
                                }.show()
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                            token.continuePermissionRequest()
                        }
                    }).check()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("KELLY","result catched")
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

            }
        }
    }


    @SuppressLint("HardwareIds")
    private fun attempt_update() {
        binding.spinKit.visibility = View.VISIBLE
        var focusView: View? = null
        var cancel = false

        if (TextUtils.isEmpty(binding.name.text.toString())) {
            binding.name.error = getString(R.string.enter_your_firstname)
            focusView = binding.name
            cancel = true
        }

        if (focusView != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                focusView.focusable
            }
        }

        if (!cancel) {
            val user = FirebaseAuth.getInstance().currentUser
            binding.spinKit.visibility = View.VISIBLE
            AndroidNetworking.post(APIURLs.BASE_URL + "users/update_profile")
                .addBodyParameter("last_name", binding.name.text.toString())
                .addBodyParameter("email", user?.email)
                .addBodyParameter("phonenumber", user?.phoneNumber)
                .addBodyParameter("gender", gender)
                .addBodyParameter("ip", Tools.getLocalIpAddress())
                .setTag("update_profile")
                .setPriority(Priority.HIGH)
                .build()
                .getAsParsed(
                        object : TypeToken<LoginResponse?>() {},
                        object : ParsedRequestListener<LoginResponse> {

                            override fun onResponse(response: LoginResponse) {
                                binding.spinKit.visibility = View.GONE
                                if (response.getStatus()) {

                                    viewLifecycleOwner.lifecycleScope.launch {
                                        insertUser(response)
                                    }

                                    val mySnackbar = Snackbar.make(binding.lytParent, getString(R.string.profile_updated), Snackbar.LENGTH_SHORT)
                                    mySnackbar.show()

                                    val intent = Intent(activity, MainActivity::class.java)
                                    startActivity(intent)

                                } else {
                                    val mySnackbar = Snackbar.make(binding.lytParent, response.message, Snackbar.LENGTH_LONG)
                                    mySnackbar.show()
                                }
                            }

                            override fun onError(anError: ANError) {
                                binding.spinKit.visibility = View.GONE
                                val mySnackbar = Snackbar.make(binding.lytParent, anError.errorBody + " hhhfjhf", Snackbar.LENGTH_SHORT)
                                mySnackbar.show()
                            }
                        })
        } else {
            focusView?.isFocused
            binding.spinKit.visibility = View.GONE
        }
    }

    @Suppress("RedundantSuspendModifier")
    suspend fun insertUser(response: LoginResponse){
        val  user = Users()
        user.name = response.name

        userViewModel.insertUser(user)
    }



    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileEditFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



}