package com.lockminds.tayari


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.ViewPager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.lockminds.tayari.adapter.SectionPagerAdapter
import com.lockminds.tayari.constants.APIURLs
import com.lockminds.tayari.constants.Constants
import com.lockminds.tayari.constants.Constants.Companion.INTENT_PARAM_1
import com.lockminds.tayari.databinding.ActivityRestaurantBinding
import com.lockminds.tayari.fragments.RestaurantTabsFragment
import com.lockminds.tayari.model.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RestaurantNearActivity : BaseActivity() {

    lateinit var binding: ActivityRestaurantBinding
    lateinit var restaurant: RestaurantNear

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        restaurant = intent.getParcelableExtra(INTENT_PARAM_1)!!
        initStatusBar()
        initComponents()
        loadImage()
    }

    override fun onResume() {
        super.onResume()
        syncDatabase()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun initComponents(){
        binding.toolbar.navigationIcon = getDrawable(R.drawable.ic_back)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = restaurant.name
        initStatusBar()
        setupViewPager(binding.viewPager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    private fun loadImage(){
        Tools.displayImageBusiness(applicationContext, binding.businessBanner, restaurant.banner)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = SectionPagerAdapter(supportFragmentManager)
        var list = listOf<Cousin>()
        GlobalScope.launch {
             list = (application as App).repository.restaurantCousins(restaurant.id.toString())
            if(list.isNotEmpty()){
                list.forEach {
                    adapter.addFragment(RestaurantTabsFragment.newInstance(it.id.toString()), it.name.toString())
                }
                runOnUiThread {
                    viewPager.adapter = adapter
                }

            }
        }

    }

    companion object {
        @JvmStatic
        fun createRestaurantNearIntent(context: Context, restaurant: RestaurantNear?): Intent {
            return Intent().setClass(context, RestaurantNearActivity::class.java)
                    .putExtra(INTENT_PARAM_1, restaurant)
        }
    }

    private fun syncDatabase(){

        val preference = application?.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE)
        if (preference != null) {

            AndroidNetworking.get(APIURLs.BASE_URL + "menu/get_all")
                    .setTag("lots")
                    .addHeaders("accept", "application/json")
                    .addHeaders("Authorization", "Bearer " + preference.getString(Constants.LOGIN_TOKEN, "false"))
                    .setPriority(Priority.HIGH)
                    .setPriority(Priority.LOW)
                    .addQueryParameter("team",restaurant.id.toString())
                    .build()
                    .getAsObjectList(Menu::class.java, object : ParsedRequestListener<List<Menu>> {
                        override fun onResponse(menu: List<Menu>) {
                            val items: List<Menu> = menu
                            if (items.isNotEmpty()) {
                                GlobalScope.launch {
                                    (application as App).repository.syncMenu(items)
                                }
                            }
                        }

                        override fun onError(anError: ANError) {}
                    })


            AndroidNetworking.get(APIURLs.BASE_URL + "menu/get_items_restaurant")
                    .setTag("menu")
                    .addQueryParameter("team",restaurant.id.toString())
                    .addHeaders("accept", "application/json")
                    .addHeaders(
                            "Authorization",
                            "Bearer " + preference.getString(Constants.LOGIN_TOKEN, "false")
                    )
                    .setPriority(Priority.HIGH)
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsObjectList(MenuItem::class.java, object : ParsedRequestListener<List<MenuItem>> {
                        override fun onResponse(menuItem: List<MenuItem>) {
                            val items: List<MenuItem> = menuItem
                            if (items.isNotEmpty()) {
                                if (items.isNotEmpty()) {
                                    GlobalScope.launch {
                                        (application as App).repository.syncMenuItems(items)
                                    }
                                }
                            }
                        }

                        override fun onError(anError: ANError) {}
                    })

        }

    }

}