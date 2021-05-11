package com.lockminds.tayari

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.lockminds.tayari.adapter.OrdersAdapter
import com.lockminds.tayari.adapter.ReposLoadStateAdapter
import com.lockminds.tayari.databinding.ActivityOrdersBinding
import com.lockminds.tayari.viewModels.OrderViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import java.lang.Runnable
import java.util.*


class OrdersActivity : BaseActivity() {

    lateinit var binding: ActivityOrdersBinding
    private lateinit var viewModel: OrderViewModel
    private val adapter = OrdersAdapter()
    private var searchJob: Job? = null

    @ExperimentalPagingApi
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        // get the view model
        viewModel = ViewModelProvider(this, Injection.orderViewModelFactory(this))
            .get(OrderViewModel::class.java)

        initComponents()
        initAdapter()
        initSearch()
        binding.retryButton.setOnClickListener {
           adapter.retry()
        }

        binding.cart.setOnClickListener {

            GlobalScope.launch {

                val cart = (application as App).repository.getOneCart()

                if(cart != null){
                    val restaurant  = (application as App).repository.getRestaurant(cart.team_id.toString())
                    runOnUiThread {
                        startActivity(
                            CartActivity.createCartIntent(
                                this@OrdersActivity,
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

            val intent = Intent(this@OrdersActivity, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.orders.setOnClickListener {
            Toast.makeText(this@OrdersActivity, "You'r Here", Toast.LENGTH_SHORT).show()
        }

        binding.home.setOnClickListener {
            val intent = Intent(this@OrdersActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    @ExperimentalPagingApi
    override fun onResume() {
        super.onResume()
        loop()
    }

    @ExperimentalPagingApi
    private fun loop() {
        CoroutineScope(IO).launch {
            delay(60000)
            CoroutineScope(Main).launch {
                getOrders()
                loop()
            }
        }
    }
    
    private fun showNoCart() {
        runOnUiThread(Runnable {
            Toast.makeText(this@OrdersActivity, "No item", Toast.LENGTH_SHORT).show()
        })
    }

    @ExperimentalPagingApi
    @SuppressLint("UseCompatLoadingForDrawables")
   fun initComponents() {
        binding.toolbar.navigationIcon = getDrawable(R.drawable.ic_baseline_arrow_back_24)
        setSupportActionBar(binding.toolbar)
        initStatusBar()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)
        binding.refresh.setOnClickListener {
            getOrders()
        }

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
            errorState?.let {}
        }

    }

    @ExperimentalPagingApi
    private fun initSearch() {
        var sessionManager = SessionManager(applicationContext)
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.getOrders(applicationContext,sessionManager.fetchId().toString()).collectLatest {
                adapter.submitData(it)
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
    private fun getOrders() {
        var sessionManager = SessionManager(applicationContext)
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.getOrders(applicationContext,sessionManager.fetchId().toString()).collectLatest {
                adapter.submitData(it)
            }
        }
    }



}
