package com.lockminds.tayari

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lockminds.tayari.adapter.*
import com.lockminds.tayari.databinding.ActivityRestaurantsBinding
import com.lockminds.tayari.viewModels.SearchRepositoriesViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch


class RestaurantsActivity : BaseActivity() {
    lateinit var binding: ActivityRestaurantsBinding
    private lateinit var viewModel: SearchRepositoriesViewModel
    private val adapter = ReposAdapter()
    private var searchJob: Job? = null

    @ExperimentalPagingApi
    private fun search() {
        // Make sure we cancel the previous job before creating a new one
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.searchRepo(applicationContext,"").collectLatest {
                adapter.submitData(it)
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables", "ResourceType")
    @ExperimentalPagingApi
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantsBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        binding.toolbar.navigationIcon = getDrawable(R.drawable.ic_back_white)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Restaurants"
        Tools.setSystemBarTransparent(this@RestaurantsActivity)
        binding.recyclerView.setLayoutManager(LinearLayoutManager(this))
        binding.recyclerView.setHasFixedSize(true)

        // get the view model
        viewModel = ViewModelProvider(this, Injection.provideViewModelFactory(this))
                .get(SearchRepositoriesViewModel::class.java)


        initAdapter()
        search()
        initSearch()
        initNav()
        binding.retryButton.setOnClickListener { adapter.retry() }
    }


    private fun initAdapter() {
        binding.recyclerView.adapter = adapter.withLoadStateFooter(
                footer = ReposLoadStateAdapter { adapter.retry() }
        )
        adapter.addLoadStateListener { loadState ->
            // Only show the list if refresh succeeds.
            binding.recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
            // Show loading spinner during initial load or refresh.
            binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            // Show the retry state if initial load or refresh fails.
            binding.retryButton.isVisible = loadState.source.refresh is LoadState.Error

            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
            val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
            errorState?.let {

            }
        }

    }

    private fun initNav(){
        binding.cart.setOnClickListener {

            GlobalScope.launch {

                val cart = (application as App).repository.getOneCart()

                if(cart != null){
                    val restaurant  = (application as App).repository.getRestaurant(cart.team_id.toString())
                    runOnUiThread {
                        startActivity(
                            CartActivity.createCartIntent(
                                this@RestaurantsActivity,
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

            val intent = Intent(this@RestaurantsActivity, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.orders.setOnClickListener {
            val intent = Intent(this@RestaurantsActivity, OrdersActivity::class.java)
            startActivity(intent)
        }

        binding.home.setOnClickListener {
            val intent = Intent(this@RestaurantsActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
    private fun showNoCart() {
        runOnUiThread(Runnable {
            Toast.makeText(this@RestaurantsActivity, "No item", Toast.LENGTH_SHORT).show()
        })
    }

    @ExperimentalPagingApi
    private fun initSearch() {

        // Scroll to top when the list is refreshed from network.
        lifecycleScope.launch {
            adapter.loadStateFlow
                    // Only emit when REFRESH LoadState for RemoteMediator changes.
                    .distinctUntilChangedBy { it.refresh }
                    // Only react to cases where Remote REFRESH completes i.e., NotLoading.
                    .filter { it.refresh is LoadState.NotLoading }
                    .collectLatest { binding.recyclerView.scrollToPosition(0) }
        }
    }


}