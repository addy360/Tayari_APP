package com.lockminds.tayari

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.reflect.TypeToken
import com.lockminds.tayari.adapter.CartMenuAdapter
import com.lockminds.tayari.adapter.TablesSpinnerAdapter
import com.lockminds.tayari.constants.APIURLs
import com.lockminds.tayari.constants.Constants
import com.lockminds.tayari.databinding.ActivityCartBinding
import com.lockminds.tayari.model.*
import com.lockminds.tayari.responses.OrderResponse
import com.lockminds.tayari.responses.Response
import com.lockminds.tayari.ui.OrderActivity
import com.lockminds.tayari.viewModels.CartViewModel
import com.lockminds.tayari.viewModels.CartViewModelFactory
import com.lockminds.tayari.viewModels.TablesViewModel
import com.lockminds.tayari.viewModels.TablesViewModelFactory
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject


class CartActivity : BaseActivity() {

    lateinit var binding: ActivityCartBinding

    private  val cartViewModel by viewModels<CartViewModel> {
        CartViewModelFactory((application as App).repository)
    }

    private  val tablesViewModel by viewModels<TablesViewModel> {
        TablesViewModelFactory((application as App).repository)
    }
    
    lateinit var restaurant: Restaurant
    lateinit var table_id: String

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        initComponents()
        restaurant  = intent.getParcelableExtra(Constants.INTENT_PARAM_1)!!
        binding.cart.setOnClickListener {

            Toast.makeText(this@CartActivity, "You'r Here", Toast.LENGTH_SHORT).show()
        }

        binding.profile.setOnClickListener {
            val intent = Intent(this@CartActivity, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.orders.setOnClickListener {
            val intent = Intent(this@CartActivity, OrdersActivity::class.java)
            startActivity(intent)
        }

        binding.home.setOnClickListener {
            val intent = Intent(this@CartActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        initCart()
        syncDatabase()
        loop()
    }

    private fun showNoCart() {
        runOnUiThread(Runnable {
            Toast.makeText(this@CartActivity, "No item", Toast.LENGTH_SHORT).show()
        })
    }
    
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initComponents() {
        binding.toolbar.navigationIcon = getDrawable(R.drawable.ic_back)
        setSupportActionBar(binding.toolbar)
        initStatusBar()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)
        binding.cartProcess.setOnClickListener {
            toggleSlideUp()
        }
        binding.slideClose.setOnClickListener {
            toggleSlideUp()
        }

        binding.confirm.setOnClickListener {
            processOrder()
        }
        val behavior = BottomSheetBehavior.from(binding.bottomSheet)
        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                //Here listen all of action bottomsheet
                when (newState) {
//                    BottomSheetBehavior.STATE_HIDDEN -> showControllers()
                    BottomSheetBehavior.STATE_EXPANDED -> hideControllers()
                    BottomSheetBehavior.STATE_COLLAPSED -> showControllers()
                }
            }
        })

    }

    private fun processOrder() {
        binding.confirm.isVisible = false
        binding.spinKit.isVisible = true
        GlobalScope.launch {
            val items: List<CartMenu> = (application as App).repository.getCartMenusList(restaurant.id.toString())
            runOnUiThread {
                val itemList = mutableListOf<OrderData>()
                var total: Float = 0F
                items.forEach {
                   itemList.add(
                       OrderData(
                           it.id.toString(),
                           it.team_id.toString(),
                           it.qty.toString(),
                           it.total.toString(),
                           it.type.toString(),
                           it.price.toString()
                       )
                   )
                    total +=  it.total.toString().toFloat()
                }

                val mapper = jacksonObjectMapper()
                val jsonArray = mapper.writeValueAsString(itemList)
                val preference = application?.getSharedPreferences(
                    Constants.PREFERENCE_KEY,
                    Context.MODE_PRIVATE
                )
                val obj = JSONObject()
                try {
                    obj.put("team",restaurant.id)
                    obj.put("table",table_id)
                    obj.put("total",total)
                    obj.put("order", jsonArray)
                } catch (e: JSONException) {

                    binding.confirm.isVisible = true
                    binding.spinKit.isVisible = false
                    e.printStackTrace()
                }

                if (preference != null) {
                    AndroidNetworking.post(APIURLs.BASE_URL + "orders/make_order")
                        .addJSONObjectBody(obj)
                        .addHeaders("accept", "application/json")
                        .addHeaders(
                            "Authorization", "Bearer " + preference.getString(
                                Constants.LOGIN_TOKEN,
                                "false"
                            )
                        )
                        .setPriority(Priority.HIGH)
                        .setPriority(Priority.LOW)
                        .build()
                        .getAsParsed(
                            object : TypeToken<OrderResponse?>() {},
                            object : ParsedRequestListener<OrderResponse> {

                                override fun onResponse(response: OrderResponse) {
                                    binding.spinKit.isVisible = false
                                    if (response.status) {

                                        Toast.makeText(
                                            this@CartActivity,
                                            response.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        GlobalScope.launch {
                                            (application as App).repository.emptyCart(restaurant.id.toString())
                                            runOnUiThread {
                                                startActivity(OrderActivity.createOrderIntent(this@CartActivity,response.order))
                                                finish()
                                            }
                                        }
                                    } else {
                                        Toast.makeText(
                                            this@CartActivity,
                                            response.message,
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        binding.confirm.isVisible = true
                                        binding.spinKit.isVisible = false
                                    }

                                }

                                override fun onError(anError: ANError) {

                                    Toast.makeText(
                                        this@CartActivity,
                                        anError.errorDetail,
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    binding.confirm.isVisible = true
                                    binding.spinKit.isVisible = false
                                }

                            })

                }else{
                    binding.confirm.isVisible = true
                    binding.spinKit.isVisible = false
                }

            }
        }
    }

    private fun showControllers(){
        binding.mainNavigation.isVisible = true
        binding.toolbar.isVisible = true
        binding.recyclerView.isVisible = true
    }

    private fun hideControllers(){
        binding.recyclerView.isVisible = false
        binding.mainNavigation.isVisible = false
        binding.toolbar.isVisible = false
    }

    private fun initCart(){

        val cartMenuAdapter = CartMenuAdapter(this)
        binding.recyclerView.adapter = cartMenuAdapter

        cartViewModel.getMenus(restaurant.id.toString()).observe(this){
            it?.let {
                    cartMenuAdapter.submitList(it)
                binding.toolbar.title = "Cart (" + it.size.toString()+") items"
                if(it.size > 0){
                    binding.confirm.isVisible = true
                }
                var total: Float = 0F
                it.forEach { it ->
                    total +=  it.total.toString().toFloat()
                }
                binding.cartTotal.text = total.toString()
            }
        }

        binding.spinKit.isVisible = false

        tablesViewModel.getTables(restaurant.id.toString()).observe(this){
            it?.let {
                val tablesAdapter = TablesSpinnerAdapter(this, it)
                (binding.table as Spinner).adapter = tablesAdapter
            }
        }

        binding.table.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {

                // It returns the clicked item.
                val clickedItem: Table =  parent.getItemAtPosition(position) as Table
                table_id = clickedItem.id.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        tools.displayImageBusiness(this, binding.logo, restaurant.logo.toString())

    }

    private fun toggleSlideUp() {
        val sheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }
    }

    private fun syncDatabase(){

        val preference = application?.getSharedPreferences(
            Constants.PREFERENCE_KEY,
            Context.MODE_PRIVATE
        )

        if (preference != null) {

            AndroidNetworking.get(APIURLs.BASE_URL + "tables/get_tables")
                .setTag("lots")
                .addHeaders("accept", "application/json")
                .addHeaders(
                    "Authorization", "Bearer " + preference.getString(
                        Constants.LOGIN_TOKEN,
                        "false"
                    )
                )
                .setPriority(Priority.HIGH)
                .setPriority(Priority.LOW)
                .build()
                .getAsObjectList(Table::class.java, object :
                    ParsedRequestListener<List<Table>> {
                    override fun onResponse(tables: List<Table>) {
                        val items: List<Table> = tables
                        if (items.isNotEmpty()) {
                            GlobalScope.launch {
                                (application as App).repository.syncTables(items)
                            }
                        }
                        binding.spinKit.isVisible = false
                    }

                    override fun onError(anError: ANError) {
                        binding.spinKit.isVisible = false
                    }
                })

        }

    }

    private fun loop() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(600000)
            CoroutineScope(Dispatchers.Main).launch {
                syncDatabase()
                loop()
            }
        }
    }

    companion object {
        @JvmStatic
        fun createCartIntent(context: Context, restaurant: Restaurant?): Intent {
            return Intent().setClass(context, CartActivity::class.java)
                .putExtra(Constants.INTENT_PARAM_1, restaurant)
        }
    }


    data class OrderData(
        val id: String,
        val team_id: String,
        val qty: String,
        val total: String,
        val type: String,
        val price: String
    ){
        constructor() : this(
            "",
            "",
            "",
            "",
            "",
            "",
        )
    }


}
