package com.lockminds.tayari

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.observe
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.GridLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.lockminds.tayari.adapter.*
import com.lockminds.tayari.constants.APIURLs
import com.lockminds.tayari.constants.Constants
import com.lockminds.tayari.constants.Constants.Companion.DATA_KEY
import com.lockminds.tayari.constants.Constants.Companion.IMAGE_URL
import com.lockminds.tayari.databinding.ActivityMainBinding
import com.lockminds.tayari.model.Restaurant
import com.lockminds.tayari.utils.ItemAnimation
import com.lockminds.tayari.viewModels.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : BaseActivity() {


    private val offersViewModel by viewModels<OffersViewModel>{
        OffersViewModelFactory( (application as App).repository)
    }

    private val nearByViewModel by viewModels<NearByViewModel>{
        NearByViewModelFactory( (application as App).repository)
    }

    private val itemsCategoryListViewModel by viewModels<ItemsCategoryListViewModel> {
        ItemsCategoryListViewModelFactory((application as App).repository)
    }

    private var restaurantAdapter: RestaurantAdapter? = null
    private val animation_type: Int = ItemAnimation.FADE_IN

     private lateinit var binding: ActivityMainBinding


     @ExperimentalPagingApi
     override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityMainBinding.inflate(layoutInflater)
            val view: View =  binding.root
            setContentView(view)
            Tools.setSystemBarLight(this)
            initComponents()
            setAdapter()
            syncDatabase()
        }

        private fun initComponents() {

            binding.recyclerCategories.layoutManager = GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
            binding.recyclerViewOffers.layoutManager = GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
            binding.recyclerNearBy.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)
            binding.recyclerRestaurants.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)

            binding.swiperefresh.setOnRefreshListener {
                binding.swiperefresh.isRefreshing = false
            }

            binding.restaurantsRight.setOnClickListener {
                val intent = Intent(applicationContext, RestaurantsActivity::class.java)
                startActivity(intent)
            }

        }


        private fun setAdapter(){
            val offersAdapter = OffersAdapter(this) { business -> adapterOnClick(business) }
            val nearByAdapter = NearByAdapter(this) { business -> adapterOnClick(business) }
            val itemsCategoriesAdapter = ItemsCategoriesAdapter(this) { business -> adapterCategoryOnClick(business) }
            binding.recyclerCategories.adapter = itemsCategoriesAdapter
            binding.recyclerViewOffers.adapter = offersAdapter
            binding.recyclerNearBy.adapter = nearByAdapter

            offersViewModel.allRestaurants.observe(this) {
                it?.let {
                    offersAdapter.submitList(it)
                }
            }

            nearByViewModel.allNearBy.observe(this) {
                it?.let {
                    nearByAdapter.submitList(it)
                }
            }

            itemsCategoryListViewModel.itemsCategoryLiveData.observe(this) {
                it?.let {
                    itemsCategoriesAdapter.submitList(it)
                }
            }


            GlobalScope.launch {
                val  restaurants = (application as App).repository.restaurants()
                if(restaurants.isNotEmpty()){
                    restaurantAdapter = RestaurantAdapter(applicationContext, restaurants, animation_type)
                    binding.recyclerRestaurants.adapter = restaurantAdapter
                    restaurantAdapter!!.setOnItemClickListener { view, obj, position ->
                        val intent = Intent(applicationContext, RestaurantActivity::class.java)
                        intent.putExtra(DATA_KEY, obj.business_key)
                        intent.putExtra(IMAGE_URL, obj.business_banner)
                        startActivity(intent)
                    }
                }
            }
            
            binding.swiperefresh.isRefreshing = false
        }


        private fun adapterOnClick(business: Restaurant) {

        }

        private fun adapterCategoryOnClick(business: Restaurant) {

        }

        private fun syncDatabase(){

                val preference = application?.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE)
                if (preference != null) {
                    AndroidNetworking.get(APIURLs.BASE_URL + "restaurants/get_all")
                        .setTag("lots")
                        .addHeaders("accept", "application/json")
                        .addHeaders("Authorization", "Bearer " + preference.getString(Constants.LOGIN_TOKEN, "false"))
                        .setPriority(Priority.HIGH)
                        .setPriority(Priority.LOW)
                        .build()
                        .getAsObjectList(Restaurant::class.java, object : ParsedRequestListener<List<Restaurant>> {
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

            }
        }


    }
