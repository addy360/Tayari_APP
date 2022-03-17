package com.user.tayari

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.GridLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.budiyev.android.codescanner.*
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.user.tayari.CousinRestaurantActivity.Companion.createCousinRestaurantIntent
import com.user.tayari.MenuOfferActivity.Companion.createMenuOfferIntent
import com.user.tayari.RestaurantActivity.Companion.createRestaurantIntent
import com.user.tayari.RestaurantNearActivity.Companion.createRestaurantNearIntent
import com.user.tayari.adapter.*
import com.user.tayari.constants.APIURLs
import com.user.tayari.constants.Constants
import com.user.tayari.data.QrRestaurantResponse
import com.user.tayari.model.*
import com.user.tayari.ui.SearchRestaurantsActivity
import com.user.tayari.viewModels.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import user.tayari.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    private val restaurantsViewModel by viewModels<RestaurantsViewModel>{
        RestaurantsViewModelFactory((application as App).repository)
    }

    private val nearByViewModel by viewModels<NearByViewModel>{
        NearByViewModelFactory((application as App).repository)
    }

    private val cousinsViewModel by viewModels<CousinsViewModel> {
        CousinsViewModelFactory((application as App).repository)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var codeScanner: CodeScanner

    @ExperimentalPagingApi
    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityMainBinding.inflate(layoutInflater)
            val view: View =  binding.root
            setContentView(view)
            binding.mainNavigation.bringToFront()
            initStatusBar()
            initComponents()
            setAdapter()
            initQr()

         binding.searchHolder.setOnClickListener {
            val intent = Intent(this@MainActivity, SearchRestaurantsActivity::class.java)
            startActivity(intent)
        }

         binding.cart.setOnClickListener {

                 GlobalScope.launch {

                     val cart = (application as App).repository.getOneCart()

                     if(cart != null){
                         val restaurant  = (application as App).repository.getRestaurant(cart.team_id.toString())
                         runOnUiThread {
                             startActivity(
                                 CartActivity.createCartIntent(
                                     this@MainActivity,
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
             val intent = Intent(this@MainActivity, ProfileActivity::class.java)
             startActivity(intent)
         }

         binding.orders.setOnClickListener {
            val intent = Intent(this@MainActivity, OrdersActivity::class.java)
            startActivity(intent)
        }

         binding.home.setOnClickListener {
            Toast.makeText(this@MainActivity, "You'r Home", Toast.LENGTH_SHORT).show()
        }



        }

    private fun showNoCart() {
        runOnUiThread(Runnable {
            Toast.makeText(this@MainActivity, "No item", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onResume() {
        super.onResume()
        syncDatabase()
        getLocation()
    }

    private fun initQr(){
        codeScanner = CodeScanner(this, binding.scannerView)
        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                binding.qr.isVisible = false
                val gson = Gson()
                val qrRestaurantResponse = gson.fromJson(
                    it.toString(),
                    QrRestaurantResponse::class.java
                )
                GlobalScope.launch {
                    val restaurant = (application as App).repository.getRestaurant(
                        qrRestaurantResponse.id
                    )
                        startActivity(createRestaurantIntent(this@MainActivity, restaurant))
                    }
                }
            }

        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                binding.qr.isVisible = false
                Toast.makeText(
                    this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.scanner.setOnClickListener {
            binding.qr.isVisible = true
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.CAMERA,
            ).withListener(object : MultiplePermissionsListener {

                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    codeScanner.startPreview()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) { /* ... */
                }
            }).check()

        }
    }

    private fun initComponents() {

            binding.recyclerCategories.layoutManager = GridLayoutManager(
                this,
                1,
                GridLayoutManager.HORIZONTAL,
                false
            )
            binding.recyclerViewOffers.layoutManager = GridLayoutManager(
                this,
                1,
                GridLayoutManager.HORIZONTAL,
                false
            )
            binding.recyclerNearBy.layoutManager = GridLayoutManager(
                this,
                2,
                GridLayoutManager.HORIZONTAL,
                false
            )
            binding.recyclerRestaurants.layoutManager = GridLayoutManager(
                this,
                3,
                GridLayoutManager.HORIZONTAL,
                false
            )

            binding.swiperefresh.setOnRefreshListener {
                binding.swiperefresh.isRefreshing = false
                syncDatabase()
            }

            binding.restaurantsRight.setOnClickListener {
                val intent = Intent(applicationContext, RestaurantsActivity::class.java)
                startActivity(intent)
            }

            binding.nearByRight.setOnClickListener {
                val intent = Intent(applicationContext, RestaurantsActivity::class.java)
                startActivity(intent)
            }


        }

    private fun setAdapter(){
            val restaurantsAdapter = RestaurantsAdapter(this) { business -> restaurantAdapterOnClick(
                business
            ) }
            val nearByAdapter = NearByAdapter(this) { business -> restaurantAdapterOnClickNear(
                business
            ) }
            val cousinAdapter = CousinsAdapter(this) { cousin -> cousinAdapterOnClick(cousin) }
            binding.recyclerCategories.adapter = cousinAdapter
            binding.recyclerNearBy.adapter = nearByAdapter
            binding.recyclerRestaurants.adapter = restaurantsAdapter

            restaurantsViewModel.allRestaurants.observe(this){
                it?.let {
                    restaurantsAdapter.submitList(it)
                }
            }

            nearByViewModel.allNearBy.observe(this) {
                it?.let {
                    nearByAdapter.submitList(it)
                }
            }

            cousinsViewModel.itemsLiveData.observe(this) {
                it?.let {
                    cousinAdapter.submitList(it)
                }
            }
            
            binding.swiperefresh.isRefreshing = false
        }

    private fun restaurantAdapterOnClick(restaurant: Restaurant) {
        startActivity(createRestaurantIntent(this, restaurant))
    }

    private fun restaurantAdapterOnClickNear(restaurant: RestaurantNear) {
        startActivity(createRestaurantNearIntent(this, restaurant))
    }

    private fun adapterOfferOnClick(obj: Menu) {
            startActivity(createMenuOfferIntent(this, obj))
        }

    private fun cousinAdapterOnClick(cousin: Cousin) {
            startActivity(createCousinRestaurantIntent(this, cousin))
        }

    private fun syncDatabase(){

            val offersAdapter = OffersAdapter(this) { offer -> adapterOfferOnClick(offer) }

                val preference = application?.getSharedPreferences(
                    Constants.PREFERENCE_KEY,
                    Context.MODE_PRIVATE
                )
                if (preference != null) {

                    AndroidNetworking.get(APIURLs.BASE_URL + "offers/get_all")
                            .setTag("lots")
                            .addHeaders("accept", "application/json")
                            .addHeaders(
                                "Authorization", "Bearer " + preference.getString(
                                    Constants.LOGIN_TOKEN,
                                    "false"
                                )
                            )
                            .setPriority(Priority.HIGH)
                            .setPriority(Priority.LOW)
                            .build()
                            .getAsObjectList(
                                Menu::class.java,
                                object : ParsedRequestListener<List<Menu>> {
                                    override fun onResponse(menu: List<Menu>) {
                                        val items: List<Menu> = menu
                                        if (items.isNotEmpty()) {
                                            binding.recyclerViewOffers.adapter = offersAdapter
                                            offersAdapter.submitList(items)
                                        }
                                    }

                                    override fun onError(anError: ANError) {}
                                })

                    AndroidNetworking.get(APIURLs.BASE_URL + "restaurants/ll")
                        .setTag("lots")
                        .addHeaders("accept", "application/json")
                        .addHeaders(
                            "Authorization", "Bearer " + preference.getString(
                                Constants.LOGIN_TOKEN,
                                "false"
                            )
                        )
                        .setPriority(Priority.HIGH)
                        .setPriority(Priority.LOW)
                        .build()
                        .getAsObjectList(
                            Restaurant::class.java,
                            object : ParsedRequestListener<List<Restaurant>> {
                                override fun onResponse(business: List<Restaurant>) {
                                    val items: List<Restaurant> = business
                                    if (items.isNotEmpty()) {

                                        GlobalScope.launch {
                                            (application as App).repository.syncRestaurants(items)
                                        }
                                    }
                                }

                                override fun onError(anError: ANError) {}
                            })

                    AndroidNetworking.get(APIURLs.BASE_URL + "cousins/get_all")
                            .setTag("cousins")
                            .addHeaders("accept", "application/json")
                            .addHeaders(
                                "Authorization", "Bearer " + preference.getString(
                                    Constants.LOGIN_TOKEN,
                                    "false"
                                )
                            )
                            .setPriority(Priority.HIGH)
                            .setPriority(Priority.LOW)
                            .build()
                            .getAsObjectList(
                                Cousin::class.java,
                                object : ParsedRequestListener<List<Cousin>> {
                                    override fun onResponse(cousin: List<Cousin>) {
                                        val items: List<Cousin> = cousin
                                        if (items.isNotEmpty()) {
                                            GlobalScope.launch {
                                                (application as App).repository.syncCousins(items)
                                            }
                                        }
                                    }

                                    override fun onError(anError: ANError) {}
                                })
                    AndroidNetworking.get(APIURLs.BASE_URL + "restaurants/get_all")
                            .setTag("lots")
                            .addHeaders("accept", "application/json")
                            .addHeaders(
                                    "Authorization", "Bearer " + preference.getString(
                                    Constants.LOGIN_TOKEN,
                                    "false"
                            )
                            )
                            .setPriority(Priority.HIGH)
                            .setPriority(Priority.LOW)
                            .build()
                            .getAsObjectList(
                                    RestaurantNear::class.java,
                                    object : ParsedRequestListener<List<RestaurantNear>> {
                                        override fun onResponse(business: List<RestaurantNear>) {
                                            val items: List<RestaurantNear> = business
                                            if (items.isNotEmpty()) {
                                                GlobalScope.launch {
                                                    (application as App).repository.syncRestaurantsNear(items)
                                                }
                                            }
                                        }

                                        override fun onError(anError: ANError) {}
                                    })

            }

        }

    private fun getLocation() {
        binding.nearBy.isVisible = false
        binding.nearByRight.isVisible = false
        binding.nearByLeft.isVisible = false
        binding.recyclerNearBy.isVisible = false
        binding.displayNearBy.isVisible =  false
    }

 }
