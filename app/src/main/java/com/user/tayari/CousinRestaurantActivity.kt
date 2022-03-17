package com.user.tayari


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
import com.user.tayari.adapter.SectionPagerAdapter
import com.user.tayari.constants.APIURLs
import com.user.tayari.constants.Constants
import com.user.tayari.constants.Constants.Companion.INTENT_PARAM_1
import com.user.tayari.fragments.RestaurantTabsFragment
import com.user.tayari.model.Cousin
import com.user.tayari.model.Menu
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import user.tayari.R
import user.tayari.databinding.ActivityRestaurantBinding

class CousinRestaurantActivity : BaseActivity() {

    lateinit var binding: ActivityRestaurantBinding
    lateinit var cousin: Cousin

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        cousin = intent.getParcelableExtra(INTENT_PARAM_1)!!
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
        binding.toolbar.navigationIcon = getDrawable(R.drawable.ic_back_white)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = null
        binding.title.text = cousin.restaurant_name
        supportActionBar!!.title = null
        tools.setSystemBarTransparent(this@CousinRestaurantActivity)
        setupViewPager(binding.viewPager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    private fun loadImage(){
        tools.displayImageBusiness(applicationContext, binding.businessBanner, cousin.restaurant_banner.toString())
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = SectionPagerAdapter(supportFragmentManager)
        var list = listOf<Cousin>()
        GlobalScope.launch {
             list = (application as App).repository.restaurantCousins(cousin.team_id.toString())
            if(list.isNotEmpty()){
                adapter.addFragment(RestaurantTabsFragment.newInstance(cousin.id.toString()), cousin.name.toString())
                list.forEach {
                    if(it.id != cousin.id){
                        adapter.addFragment(RestaurantTabsFragment.newInstance(it.id.toString()), it.name.toString())
                    }
                }
                runOnUiThread {
                    viewPager.adapter = adapter
                }

            }
        }

    }

    companion object {
        @JvmStatic
        fun createCousinRestaurantIntent(context: Context, cousin: Cousin?): Intent {
            return Intent().setClass(context, CousinRestaurantActivity::class.java)
                    .putExtra(INTENT_PARAM_1, cousin)
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
                    .addQueryParameter("team",cousin.team_id.toString())
                    .build()
                    .getAsObjectList(Menu::class.java, object : ParsedRequestListener<List<Menu>> {
                        override fun onResponse(menu: List<Menu>) {
                            val items: List<Menu> = menu
                            if (items.isNotEmpty()) {
                                GlobalScope.launch {
                                    (application as App).repository.syncMenu(cousin.team_id.toString(),items)
                                }
                            }
                        }

                        override fun onError(anError: ANError) {}
                    })
        }

    }

}