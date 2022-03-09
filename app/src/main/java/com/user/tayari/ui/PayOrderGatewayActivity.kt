package com.user.tayari.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import androidx.core.view.isVisible
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.ParsedRequestListener
import com.google.gson.reflect.TypeToken
import com.user.tayari.*
import com.user.tayari.constants.APIURLs
import com.user.tayari.constants.Constants
import com.user.tayari.databinding.ActivityPayOrderGatewayBinding
import com.user.tayari.databinding.PaymentSuccessBinding
import com.user.tayari.model.Order
import com.user.tayari.model.Parameters
import com.user.tayari.responses.Response
import com.user.tayari.responses.TigoTokenResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*

class PayOrderGatewayActivity : BaseActivity() {
    lateinit var order: Order
    lateinit var binding: ActivityPayOrderGatewayBinding
    var START_MILLI_SECONDS = 60000L
    lateinit var countDownTimer: CountDownTimer
    var isRunning: Boolean = false
    var timer_in_milli_seonds = 0L
    lateinit var alertDialog: AlertDialog
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
       /* binding.payNow.setOnClickListener {
            payWithTigo()
        }*/

        binding.payNow.isVisible = false
        binding.timerText.isVisible = false
        requestTigoToken();
        binding.totalRight.text = "${order.balance}${order.currency}"
        binding.phoneNumber.setText(sessionManager.fetchPhonenumber().toString())

        if (order.payment_status == "complete"){
            binding.payNow.text = "Paid"
            binding.payNow.isEnabled = false
        }

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

    private fun timer(){
        if (!isRunning){
            timer_in_milli_seonds = 5 * 60000L
            binding.timerText.isVisible = true;
            startTimer(timer_in_milli_seonds)
        }
    }

    private fun startTimer(timer_in_seconds: Long){
        binding.payNow.isVisible = false
        countDownTimer = object :
        CountDownTimer(timer_in_seconds, 1000){
            override fun onTick(p0: Long) {
               timer_in_milli_seonds = p0
                updateTextUI()
            }

            override fun onFinish() {
                print("count finished")
                binding.payNow.text = "Retry"
                binding.payNow.isVisible = true
            }

        }
        countDownTimer.start()
        isRunning = true
    }

    private fun updateTextUI(){
        val minute = (timer_in_milli_seonds / 1000) / 60
        val second = (timer_in_milli_seonds / 1000) % 60
        binding.timerText.text = "$minute:$second"
        when(minute.toInt()){
            1 -> checkPayment()
            2 -> checkPayment()
            3 -> checkPayment()
            4 -> checkPayment()
            5 -> checkPayment()
        }
    }

  /*  private fun payWithTigo() {
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

    }*/

    private fun payWithTigo(token: String) {
        binding.spinKit.isVisible = true
      val obj =  JSONObject()
      obj.put("CustomerMSISDN", binding.phoneNumber.text.toString())
      obj.put("BillerMSISDN", APIURLs.PAY_BILLER)
      obj.put("Amount", order.balance)
      obj.put("Remarks", "Food purchase")
      obj.put("ReferenceID", "TYP"+ UUID.randomUUID().toString())
        AndroidNetworking.post(APIURLs.TIGO_PUSH_BILL)
            .addJSONObjectBody(obj)
            .addHeaders("accept", "application/json")
            .addHeaders("username", APIURLs.PAY_USERNAME)
            .addHeaders("Cache-Control", "no-cache")
            .addHeaders("password", APIURLs.PAY_PASSWORD)
            .addHeaders(
                "Authorization", "Bearer $token")
            .setPriority(Priority.HIGH)
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(
                object : JSONObjectRequestListener{
                    override fun onResponse(response: JSONObject?) {
                        Log.d("onResponse: ", response.toString())
                        binding.spinKit.isVisible = false
                        Log.d("onResponse: ", response.toString())
                        Toast.makeText(
                            this@PayOrderGatewayActivity,
                            response.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        if(response?.getBoolean("ResponseStatus") == true) {
                            timer()
                            savePaymentResponse(response)
                        }
                    }

                    override fun onError(anError: ANError?) {
                        binding.spinKit.isVisible = false
                        anError?.errorDetail?.let { Log.d("onError: ", it) }
                        Toast.makeText(
                            this@PayOrderGatewayActivity,
                            anError?.errorDetail,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            )

    }

    private fun requestTigoToken() {
        binding.spinKit.isVisible = true
       val map = mapOf("username" to APIURLs.PAY_USERNAME, "password" to APIURLs.PAY_PASSWORD, "grant_type" to "password")

        AndroidNetworking.post(APIURLs.TIGO_TOKEN)
           // .addUrlEncodeFormBodyParameter(map)
            .addUrlEncodeFormBodyParameter("username", APIURLs.PAY_USERNAME)
            .addUrlEncodeFormBodyParameter("password", APIURLs.PAY_PASSWORD)
            .addUrlEncodeFormBodyParameter("grant_type", "password")
            .setPriority(Priority.HIGH)
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener{
                override fun onResponse(response: JSONObject?) {
                    binding.spinKit.isVisible = false
                    Log.d("onResponse: ", response.toString())
                    binding.payNow.isVisible = true

                    binding.payNow.setOnClickListener {
                        response?.getString("access_token")?.let { it1 -> payWithTigo(it1) }
                    }
                }

                override fun onError(anError: ANError?) {
                    binding.spinKit.isVisible = false
                    anError?.errorDetail?.let { Log.d("onError: ", it) }
                        Toast.makeText(
                            this@PayOrderGatewayActivity,
                            anError?.errorDetail,
                            Toast.LENGTH_SHORT
                        ).show()

                }

            })


    }

    private fun savePaymentResponse(json: JSONObject){
        val obj =  JSONObject()
        obj.put("CustomerMSISDN", binding.phoneNumber.text.toString())
        obj.put("orderID", order.id)
        obj.put("amount", order.balance)
        obj.put("ReferenceID", json.getString("ReferenceID"))
        obj.put("ResponseCode", json.getString("ResponseCode"))
        obj.put("ResponseDescription", json.getString("ResponseDescription"))
        obj.put("ResponseStatus", json.getBoolean("ResponseStatus"))

        AndroidNetworking.post(APIURLs.BASE_URL+APIURLs.PAYMENT_SAVE)
            .addJSONObjectBody(obj)
            .addHeaders("accept", "application/json")
            .setPriority(Priority.HIGH)
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener{
                override fun onResponse(response: JSONObject?) {
                    Log.d( "onSave: ", response.toString())
                }

                override fun onError(anError: ANError?) {
                    Log.d("onError: ", anError?.message.toString())
                }

            })

    }

    private fun checkPayment(){
        val obj =  JSONObject()
        obj.put("id", order.id)

        AndroidNetworking.post(APIURLs.BASE_URL+APIURLs.PAYMENT_CHECK)
            .addJSONObjectBody(obj)
            .addBodyParameter("id", order.id.toString())
            .addHeaders("accept", "application/json")
            .setPriority(Priority.HIGH)
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener{
                override fun onResponse(response: JSONObject?) {
                    Log.d( "onCheck: ", response.toString())
                    if (response?.getBoolean("status") == true){
                        Toast.makeText(
                            this@PayOrderGatewayActivity,
                            "Payment successfully paid",
                            Toast.LENGTH_SHORT
                        ).show()
                        countDownTimer.cancel()
                        isRunning = false
                        binding.timerText.isVisible = false
                        paymentSuccess(binding.root)
                    }
                }

                override fun onError(anError: ANError?) {
                    Log.d("onError: ", anError?.message.toString())
                }

            })

    }

    private fun showNoCart() {
        runOnUiThread(Runnable {
            toast("No item")
        })
    }

    private fun paymentSuccess(view: View){
        var alert = AlertDialog.Builder(view.context)
        var payBinding: PaymentSuccessBinding = PaymentSuccessBinding.inflate(layoutInflater)
        alert.setView(payBinding.root)
        payBinding.okayButton.setOnClickListener {
            alertDialog.dismiss()
            val intent = Intent(this@PayOrderGatewayActivity, MainActivity::class.java)
            startActivity(intent)
        }
        alertDialog = alert.create()
        alertDialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        alertDialog.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        alertDialog.show()
    }

    companion object{
        @JvmStatic
        fun createOrderIntent(context: Context,order: Order?): Intent{
            return Intent().setClass(context,PayOrderGatewayActivity::class.java).
            putExtra(Constants.INTENT_PARAM_1,order)
        }
    }
}