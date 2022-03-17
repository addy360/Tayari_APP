package com.user.tayari.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.user.tayari.*
import com.user.tayari.adapter.OrderItemsAdapter
import com.user.tayari.adapter.ReposLoadStateAdapter
import com.user.tayari.constants.Constants.Companion.INTENT_PARAM_1
import com.user.tayari.model.Order
import com.user.tayari.viewModels.OrderItemViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import user.tayari.databinding.ActivityOrderBinding


class OrderActivity : BaseActivity() {

    lateinit var binding: ActivityOrderBinding
    private lateinit var viewModel: OrderItemViewModel
            lateinit var order: Order
    private var searchJob: Job? = null
    private val adapter = OrderItemsAdapter()

    @ExperimentalPagingApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding = ActivityOrderBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        initComponents()
        initNav()
        initAdapter()
        initItems()
    }

    @ExperimentalPagingApi
    @SuppressLint("UseCompatLoadingForDrawables")
    fun initComponents() {
        order = intent.getParcelableExtra(INTENT_PARAM_1)!!
        // get the view model
        viewModel = ViewModelProvider(this, Injection.orderItemsViewModelFactory(this))
                .get(OrderItemViewModel::class.java)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = null
        binding.title.text = order.restaurant_name
        binding.orderId.text = "#: "+order.id + " "+order.order_status
        binding.orderPrice.text = "cost: "+order.cost+order.currency
        binding.orderTable.text = "Table: "+order.table_name
        binding.orderDate.text = order.created_at
        binding.orderPaid.text = "Paid "+order.paid+order.currency
        binding.orderBalance.text = "Balance "+order.balance+order.currency
        tools.displayImageBusiness(applicationContext, binding.businessBanner, order.restaurant_banner.toString())
        tools.setSystemBarTransparent(this@OrderActivity)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)

        if(order.balance.toString() == "0" || order.balance.toString() == "0.0"){
            binding.payNow.isVisible = false
        }

        if(order.payment_status == "complete"){
            binding.payNow.text = "Paid"
            binding.payNow.isEnabled = false
        }

        binding.navBack.setOnClickListener {
            onBackPressed()
        }

        binding.payNow.setOnClickListener {
            startActivity(PayOrderGatewayActivity.createOrderIntent(this@OrderActivity,order))
        }
    }



    @ExperimentalPagingApi
    private fun initItems() {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.getOrderItems(applicationContext,order.id.toString()).collectLatest {
                adapter.submitData(it)
            }
        }

        // Scroll to top when the list is refreshed from network.
//        lifecycleScope.launch {
//            adapter.loadStateFlow
//                    // Only emit when REFRESH LoadState for RemoteMediator changes.
//                    .distinctUntilChangedBy { it.refresh }
//                    // Only react to cases where Remote REFRESH completes i.e., NotLoading.
//                    .filter { it.refresh is LoadState.NotLoading }
//                    .collectLatest { binding.recyclerView.scrollToPosition(0) }
//        }
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
            errorState?.let {}
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
                                        this@OrderActivity,
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

            val intent = Intent(this@OrderActivity, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.orders.setOnClickListener {
            val intent = Intent(this@OrderActivity, OrdersActivity::class.java)
            startActivity(intent)
        }

        binding.home.setOnClickListener {
            val intent = Intent(this@OrderActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showNoCart() {
        runOnUiThread(Runnable {
            Toast.makeText(this@OrderActivity, "No item", Toast.LENGTH_SHORT).show()
        })
    }


    companion object{
        @JvmStatic
        fun createOrderIntent(context: Context,order: Order?): Intent{
            return Intent().setClass(context,OrderActivity::class.java).
                    putExtra(INTENT_PARAM_1,order)
        }
    }

}