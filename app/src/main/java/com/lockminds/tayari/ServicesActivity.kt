package com.lockminds.tayari

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lockminds.tayari.constants.Constants
import com.lockminds.tayari.data.Service
import com.lockminds.tayari.databinding.ActivityServicesBinding
import com.lockminds.tayari.fragments.ProductsFragment
import com.lockminds.tayari.fragments.ServicesFragment
import com.lockminds.tayari.viewModels.BusinessDetailsViewModel
import com.lockminds.tayari.viewModels.BusinessDetailsViewModelFactory
import com.lockminds.tayari.viewModels.ServicesListViewModel
import com.lockminds.tayari.viewModels.ServicesListViewModelFactory

lateinit var binding: ActivityServicesBinding

class ServicesActivity : BaseActivity() {

    private val businessModel by viewModels<BusinessDetailsViewModel>{
        BusinessDetailsViewModelFactory(this)
    }
    private val servicesListViewModel by viewModels<ServicesListViewModel> {
        ServicesListViewModelFactory(this)
    }

    private lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServicesBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            binding.toolbar.navigationIcon = getDrawable(R.drawable.ic_back_white)
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = null
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.collapsingToolbarLayout.title = getString(R.string.app_name)
        binding.collapsingToolbarLayout.setCollapsedTitleTextColor(0xffffff)

        var currentBusinessKey: String?= null
        val bundle: Bundle? = intent.extras
        currentBusinessKey = bundle?.getString(Constants.BUSINESS_KEY)
        val fragment = ServicesFragment.newInstance(currentBusinessKey.toString())
        replaceFragment(fragment)
    }

    override fun onResume() {
        super.onResume()
        businessDetails()
    }

    private fun adapterOnClick(service: Service) {

    }

    private fun businessDetails() {
        var currentBusinessKey: String?= null
        val bundle: Bundle? = intent.extras
        currentBusinessKey = bundle?.getString(Constants.BUSINESS_KEY)
        currentBusinessKey?.let{
            var currentBusiness = businessModel.getBusinessForKey(currentBusinessKey)
            Tools.displayImageBusiness(this, binding.businessBanner, currentBusiness?.business_banner)
            binding.collapsingToolbarLayout.title =currentBusiness?.business_name  + " - Menu"
        }
    }

    fun onButtonTabClick(v: View) {
        var currentBusinessKey: String?= null
        val bundle: Bundle? = intent.extras
        currentBusinessKey = bundle?.getString(Constants.NEWS_KEY)
        var title: String? = (v as Button).text.toString()
        title = Tools.toCamelCase(title)
        when (v.getId()) {
            R.id.tab_btn_menus -> {
                // Create new fragment
                var currentBusiness = businessModel.getBusinessForKey(currentBusinessKey.toString())
                binding.collapsingToolbarLayout.title =currentBusiness?.business_name + " - Menu"
                val fragment = ServicesFragment.newInstance(currentBusinessKey.toString())
                replaceFragment(fragment)
            }
            R.id.tab_btn_products-> {
                // Create new fragment
                var currentBusiness = businessModel.getBusinessForKey(currentBusinessKey.toString())
                binding.collapsingToolbarLayout.title =currentBusiness?.business_name + " - Products"
                val fragment = ProductsFragment.newInstance(currentBusinessKey.toString())
                replaceFragment(fragment)
            }


        }
        supportActionBar!!.setTitle(title)
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container_view, fragment)
        transaction.commit()
    }

}