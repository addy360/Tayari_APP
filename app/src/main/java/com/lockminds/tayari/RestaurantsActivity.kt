package com.lockminds.tayari

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.lockminds.tayari.adapter.*
import com.lockminds.tayari.constants.APIURLs
import com.lockminds.tayari.constants.Constants
import com.lockminds.tayari.databinding.ActivityRestaurantsBinding
import com.lockminds.tayari.model.Restaurant
import com.lockminds.tayari.utils.ItemAnimation
import com.mancj.materialsearchbar.MaterialSearchBar


class RestaurantsActivity : BaseActivity(), MaterialSearchBar.OnSearchActionListener {
    lateinit var binding: ActivityRestaurantsBinding
    private var restaurantAdapter: RestaurantAllAdapter? = null
    private val animation_type: Int = ItemAnimation.FADE_IN
    @ExperimentalPagingApi
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantsBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        initComponents()
        initSearch()
    }

    private fun initSearch() {
        binding.searchBar.setOnSearchActionListener(this@RestaurantsActivity)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initComponents(){
        initStatusBar()
        binding.toolbar.navigationIcon = getDrawable(R.drawable.ic_back)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = getString(R.string.restaurants)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        Tools.setSystemBarColor(this, R.color.colorPrimaryDark)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)

        binding.searchBar.addTextChangeListener(object : TextWatcher  {

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                restaurantAdapter?.filter?.filter(binding.searchBar.text.trim())
            }

            override fun afterTextChanged(editable: Editable) {}

        })

        binding.swiperefresh.setOnRefreshListener {
            setAdapter()
        }

        binding.spinKit.isVisible = true
        setAdapter()

    }


    private fun setAdapter(){
        binding.spinKit.isVisible = true
        val preference = application?.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE)
        if (preference != null) {
            AndroidNetworking.get(APIURLs.BASE_URL + "restaurants/get_all")
                .setTag("lots")
                .addHeaders("accept", "application/json")
                .addHeaders("Authorization", "Bearer " + preference.getString(Constants.LOGIN_TOKEN, "false"))
                .setPriority(Priority.HIGH)
                .setPriority(Priority.LOW)
                .build()
                .getAsObjectList(Restaurant::class.java, object :
                    ParsedRequestListener<List<Restaurant>> {
                    override fun onResponse(business: List<Restaurant>) {
                        val items: List<Restaurant> = business
                        if (items.isNotEmpty()) {
                            restaurantAdapter = RestaurantAllAdapter(applicationContext,items, animation_type)
                            binding.recyclerView.adapter = restaurantAdapter
                            restaurantAdapter!!.setOnItemClickListener { view, obj, position ->
                                startActivity(RestaurantActivity.createRestaurantIntent(this@RestaurantsActivity, obj))
                            }
                        }
                    }

                    override fun onError(anError: ANError) {}
                })

        }

        binding.spinKit.isVisible = false

    }

    override fun onSearchStateChanged(enabled: Boolean) {

    }

    override fun onSearchConfirmed(text: CharSequence?) {

    }

    override fun onButtonClicked(buttonCode: Int) {

    }

}