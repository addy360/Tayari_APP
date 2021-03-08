package com.lockminds.tayari

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.google.firebase.auth.FirebaseAuth
import com.lockminds.tayari.adapter.BusinessAdapter
import com.lockminds.tayari.adapter.ItemsCategoriesAdapter
import com.lockminds.tayari.constants.Constants.Companion.BUSINESS_KEY
import com.lockminds.tayari.data.Business
import com.lockminds.tayari.data.ItemCategory
import com.lockminds.tayari.databinding.ActivityMainBinding
import com.lockminds.tayari.firebase.ui.auth.AuthUiActivity
import com.lockminds.tayari.viewModels.*


class MainActivity : BaseActivity() {

    private val userViewModel: UsersViewModel by viewModels {
        UsersViewModelFactory((application as App).repository)
    }

    val businessListViewModel by viewModels<BusinessListViewModel>{
        BusinessListViewModelFactory(this)
    }

    val itemsCategoryListViewModel by viewModels<ItemsCategoryListViewModel> {
        ItemsCategoryListViewModelFactory(this)
    }

        private lateinit var binding: ActivityMainBinding
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityMainBinding.inflate(layoutInflater)
            val view: View =  binding.root
            setContentView(view)
            Tools.setSystemBarLight(this)

//        setSupportActionBar(binding.toolbar)
//        initNavigation(binding.navigation)
//        binding.toolbar.title = getString(R.string.app_name)
//        userViewModel.user.observe(this, Observer { user ->
//            user?.let {
//                binding.toolbar.title = user.first_name
//            }
//        })

            initComponents()

        }

        private fun initComponents() {

            val businessAdapter = BusinessAdapter(this) { business -> adapterOnClick(business) }
            val itemsCategoriesAdapter = ItemsCategoriesAdapter(this) { business -> adapterCategoryOnClick(business) }

            binding.recyclerView.layoutManager = GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
            binding.recyclerView.adapter = businessAdapter

            binding.recyclerCategories.layoutManager = GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
            binding.recyclerCategories.adapter = itemsCategoriesAdapter

            businessListViewModel.businessLiveData.observe(this, {
                it?.let {
                    businessAdapter.submitList(it as MutableList<Business>)
                }
            })

            itemsCategoryListViewModel.ItemsCategoryLiveData.observe(this,{
                it?.let {
                    itemsCategoriesAdapter.submitList(it as MutableList<ItemCategory>)
                }
            })

        }

        override fun onResume() {
            super.onResume()
            val auth = FirebaseAuth.getInstance()

            if (auth.currentUser == null) {
                val intent = Intent(applicationContext, AuthUiActivity::class.java)
                startActivity(intent)
                finish()
            }

            fetchBusiness();
        }

        private fun fetchBusiness() {
            AndroidNetworking.get("https://api.tayari.co.tz/api/business/get_business")
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
                .getAsObjectList(Business::class.java, object : ParsedRequestListener<List<Business>> {
                    override fun onResponse(business: List<Business>) {
                        val items: List<Business> = business
                        if (items.size > 0) {
                            businessListViewModel.insertBusinessList(items)
                            binding.recyclerView.bringToFront()
                        }
                    }

                    override fun onError(anError: ANError) {
                    }
                })
        }

        private fun adapterOnClick(business: Business) {
            val intent = Intent(this, ServicesActivity::class.java)
            intent.putExtra(BUSINESS_KEY, business.business_key)
            startActivity(intent)
        }

    private fun adapterCategoryOnClick(business: ItemCategory) {
        val intent = Intent(this, ServicesActivity::class.java)
        intent.putExtra(BUSINESS_KEY, business.business_key)
        startActivity(intent)
    }

    }
