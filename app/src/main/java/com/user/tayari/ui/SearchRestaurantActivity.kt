package com.user.tayari.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.user.tayari.*
import com.user.tayari.adapter.*
import com.user.tayari.databinding.ActivitySearchRestaurantBinding
import com.user.tayari.viewModels.SearchRepositoriesViewModel
import com.user.tayari.viewModels.SearchRestaurantViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch


class SearchRestaurantsActivity : BaseActivity() {
    lateinit var binding: ActivitySearchRestaurantBinding
    private lateinit var viewModel: SearchRestaurantViewModel
    private val adapter = SearchRestaurantAdapter()
    private var searchJob: Job? = null

    @ExperimentalPagingApi
    private fun search(query: String) {
        // Make sure we cancel the previous job before creating a new one
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.searchRest(applicationContext,query).collectLatest {
                adapter.submitData(it)
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables", "ResourceType")
    @ExperimentalPagingApi
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchRestaurantBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        binding.toolbar.navigationIcon = getDrawable(R.drawable.ic_back)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        initStatusBar()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)

        // get the view model
        viewModel = ViewModelProvider(this, Injection.provideSearchRestaurantViewModelFactory(this))
                .get(SearchRestaurantViewModel::class.java)

        initAdapter()
        initSearch()
        search("")
        initNav()
        binding.retryButton.setOnClickListener { adapter.retry() }
    }


    private fun initAdapter() {
        binding.recyclerView.adapter = adapter.withLoadStateFooter(
                footer = ReposLoadStateAdapter { adapter.retry() }
        )
        adapter.addLoadStateListener { loadState ->
            // Only show the list if refresh succeeds.
            binding.recyclerView.isVisible = true
            //loadState.source.refresh is LoadState.NotLoading
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
                                this@SearchRestaurantsActivity,
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

            val intent = Intent(this@SearchRestaurantsActivity, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.orders.setOnClickListener {
            val intent = Intent(this@SearchRestaurantsActivity, OrdersActivity::class.java)
            startActivity(intent)
        }

        binding.home.setOnClickListener {
            val intent = Intent(this@SearchRestaurantsActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
    private fun showNoCart() {
        runOnUiThread(Runnable {
            Toast.makeText(this@SearchRestaurantsActivity, "No item", Toast.LENGTH_SHORT).show()
        })
    }

    @ExperimentalPagingApi
    private fun initSearch() {

        binding.searchRepo.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput()
                true
            } else {
                false
            }
        }

        binding.searchRepo.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepoListFromInput()
                true
            } else {
                false
            }
        }

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

    @ExperimentalPagingApi
    private fun updateRepoListFromInput() {
        binding.searchRepo.text.trim().let {
            if (it.isNotEmpty()) {
                search(it.toString())
            }
        }
    }


}