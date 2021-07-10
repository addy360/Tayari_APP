package com.lockminds.tayari.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.google.gson.reflect.TypeToken
import com.lockminds.tayari.*
import com.lockminds.tayari.constants.APIURLs
import com.lockminds.tayari.constants.Constants
import com.lockminds.tayari.databinding.ActivityPayOrderGatewayBinding
import com.lockminds.tayari.model.Order
import com.lockminds.tayari.model.Parameters
import com.lockminds.tayari.responses.Response
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PayOrderGatewayActivity : BaseActivity() {
    lateinit var order: Order
    lateinit var binding: ActivityPayOrderGatewayBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOrderGatewayBinding.inflate(layoutInflater)
        val view: View =  binding.root
        setContentView(view)
        binding.toolbar.navigationIcon = getDrawable(R.drawable.ic_baseline_arrow_back_24)
        binding.toolbar.title =""
        setSupportActionBar(binding.toolbar)
        initStatusBar()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        order = intent.getParcelableExtra(Constants.INTENT_PARAM_1)!!
        binding.payNow.setOnClickListener {
            payWithTigo()
        }
        binding.totalRight.text = "${order.balance}${order.currency}"
        binding.phoneNumber.setText(sessionManager.fetchPhonenumber().toString())

        binding.cart.setOnClickListener {

            GlobalScope.launch {

                val cart = (application as App).repository.getOneCart()

                if(cart != null){
                    val restaurant  = (application as App).repository.getRestaurant(cart.team_id.toString())
                    runOnUiThread {
                        startActivity(
                            CartActivity.createCartIntent(
                                this@PayOrderGatewayActivity,
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
            val intent = Intent(this@PayOrderGatewayActivity, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.orders.setOnClickListener {
            val intent = Intent(this@PayOrderGatewayActivity, OrdersActivity::class.java)
            startActivity(intent)
        }

        binding.home.setOnClickListener {
            val intent = Intent(this@PayOrderGatewayActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun payWithTigo() {
        binding.spinKit.isVisible = true
        AndroidNetworking.post(APIURLs.BASE_URL + "orders/pay_order")
            .addBodyParameter("order",order.id.toString())
            .addBodyParameter("account",binding.phoneNumber.text.toString())
            .addHeaders("accept", "application/json")
            .addHeaders(
                "Authorization", "Bearer " + sessionManager.fetchAuthToken()
            )
            .setPriority(Priority.HIGH)
            .setPriority(Priority.LOW)
            .build()
            .getAsParsed(
                object : TypeToken<Response?>() {},
                object : ParsedRequestListener<Response> {

                    override fun onResponse(response: Response) {

                        binding.spinKit.isVisible = false

                        if (response.status) {
                            val parameters = Parameters(response.message)
                            startActivity(BrowserActivity.createOrderIntent(this@PayOrderGatewayActivity,parameters))
                            finish()
                        } else {
                            Toast.makeText(
                                this@PayOrderGatewayActivity,
                                response.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onError(anError: ANError) {
                        binding.spinKit.isVisible = false
                        Toast.makeText(
                            this@PayOrderGatewayActivity,
                            anError.errorDetail,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })

    }

    private fun showNoCart() {
        runOnUiThread(Runnable {
            toast("No item")
        })
    }

    companion object{
        @JvmStatic
        fun createOrderIntent(context: Context,order: Order?): Intent{
            return Intent().setClass(context,PayOrderGatewayActivity::class.java).
            putExtra(Constants.INTENT_PARAM_1,order)
        }
    }
}