package com.lockminds.tayari


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.lockminds.tayari.constants.Constants.Companion.DATA_KEY
import com.lockminds.tayari.constants.Constants.Companion.IMAGE_URL
import com.lockminds.tayari.databinding.ActivityRestaurantBinding
import com.lockminds.tayari.fragments.RestaurantTabsFragment
import com.lockminds.tayari.model.Restaurant
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class RestaurantActivity : BaseActivity() {

    lateinit var binding: ActivityRestaurantBinding
    lateinit var restaurant: Restaurant

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        initComponents()
        initRestaurant()
        loadImage()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun initComponents(){
        binding.toolbar.navigationIcon = getDrawable(R.drawable.ic_back)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = null
        Tools.setSystemBarLight(this)
        Tools.setSystemBarTransparent(this)
        setupViewPager(binding.viewPager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.spinKit.bringToFront()
    }

    private fun initRestaurant(){
        val bundle: Bundle? = intent.extras
        val key = bundle?.getString(DATA_KEY)
        GlobalScope.launch {
            restaurant = key?.let { (application as App).repository.getRestaurant(it) }!!
            supportActionBar!!.title  = restaurant.business_name
        }
    }

    private fun loadImage(){
        val bundle: Bundle? = intent.extras
        val url = bundle?.getString(IMAGE_URL)
        Tools.displayImageBusiness(applicationContext, binding.businessBanner, url)
    }


    private fun setupViewPager(viewPager: ViewPager) {
        val bundle: Bundle? = intent.extras
        val adapter = SectionsPagerAdapter(supportFragmentManager)
        adapter.addFragment(RestaurantTabsFragment.newInstance(bundle?.getString(DATA_KEY).toString(),binding.spinKit), "African")
        adapter.addFragment(RestaurantTabsFragment.newInstance(bundle?.getString(DATA_KEY).toString(),binding.spinKit), "Asian")
        adapter.addFragment(RestaurantTabsFragment.newInstance(bundle?.getString(DATA_KEY).toString(),binding.spinKit), "American")
        adapter.addFragment(RestaurantTabsFragment.newInstance(bundle?.getString(DATA_KEY).toString(),binding.spinKit), "Brazilian")
        adapter.addFragment(RestaurantTabsFragment.newInstance(bundle?.getString(DATA_KEY).toString(),binding.spinKit), "Kisamvu")
        viewPager.adapter = adapter
    }


    private class SectionsPagerAdapter(manager: FragmentManager?) :
        FragmentPagerAdapter(manager!!) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

}