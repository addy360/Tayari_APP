package com.lockminds.tayari

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.lockminds.tayari.constants.APIURLs
import com.lockminds.tayari.constants.Constants
import com.lockminds.tayari.data.QrRestaurantResponse
import com.lockminds.tayari.databinding.ActivityServiceDetailsBinding
import com.lockminds.tayari.datasource.myMoney
import com.lockminds.tayari.fragments.RestaurantTabsFragment
import com.lockminds.tayari.model.Menu
import com.lockminds.tayari.model.MenuItem
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MenuActivity : BaseActivity() {

    lateinit var binding: ActivityServiceDetailsBinding
    lateinit var menu: Menu
    lateinit var currentView: CheckBox

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceDetailsBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        database = Firebase.database.reference
        menu = intent.getParcelableExtra(Constants.INTENT_PARAM_1)!!
        initComponents()
    }

    override fun onResume() {
        super.onResume()
        initCheckBox()
        initCartActions()
        initCart()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initComponents() {
        binding.toolbar.navigationIcon = getDrawable(R.drawable.ic_back_white)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = menu.name
        initStatusBar()
        Tools.displayImageBusiness(applicationContext, binding.image, menu.image)
        initMenu()
        binding.slideClose.setOnClickListener {
            toggleSlideUp()
        }

        binding.cartIncreaseItem.setOnClickListener {
            val preference = application?.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE)
            if (preference != null) {
                val userid = preference.getString(Constants.USER_ID, "false").toString()
                if(userid.isNotEmpty()){
                    menu.qty = (binding.cartCountItem.text.toString().toFloat() + 1).toString()
                    menu.total = (menu.qty.toString().toFloat()* menu.price.toString().toFloat()).toString()
                    database.child(userid).child("cart").child(menu.id.toString()).setValue(menu)
                            .addOnSuccessListener {
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@MenuActivity, "Some thing went wrong", Toast.LENGTH_SHORT).show()
                            }
                }
            }
        }

        binding.cartDecreaseItem.setOnClickListener {
            val preference = application?.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE)
            if (preference != null) {
                val userid = preference.getString(Constants.USER_ID, "false").toString()
                if(userid.isNotEmpty()){
                    menu.qty = (binding.cartCountItem.text.toString().toFloat() - 1).toString()
                    if(menu.qty.toString().equals("-1.0")){
                        database.child(userid).child("cart").child(userid).child(menu.id.toString()).setValue(null)
                                .addOnSuccessListener { }
                                .addOnFailureListener {
                                    Toast.makeText(this@MenuActivity, "Some thing went wrong", Toast.LENGTH_SHORT).show()
                                }
                    }else if(menu.qty.toString().equals("0.0")){
                        database.child(userid).child("cart").child(menu.id.toString()).setValue(null)
                                .addOnSuccessListener {}
                                .addOnFailureListener {
                                    Toast.makeText(this@MenuActivity, "Some thing went wrong", Toast.LENGTH_SHORT).show()
                                }
                    }else{
                        menu.total = (menu.qty.toString().toFloat()* menu.price.toString().toFloat()).toString()
                        database.child(userid).child("cart").child(menu.id.toString()).setValue(menu)
                                .addOnSuccessListener {}
                                .addOnFailureListener {
                                    Toast.makeText(this@MenuActivity, "Some thing went wrong", Toast.LENGTH_SHORT).show()
                                }
                    }


                }
            }
        }

    }

    private fun toggleSlideUp() {
        val sheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }
    }

    private fun initCart(){
        val preference = application?.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE)
        if (preference != null) {

            var grandTotal : Float = 10F

            val userId = preference.getString(Constants.USER_ID, "false").toString()

            val chRef = database.child(userId).child("cart_items")
            val postListener = object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    val response = dataSnapshot.getValue<MenuItem>()
                    if (response != null) {
                            binding.cartSize.text = dataSnapshot.childrenCount.toString()
                        }else{
                            binding.cartSize.text = "00"
                    }

                    if(response != null){
                        for (postSnapshot in dataSnapshot.children) {
                            val resp = postSnapshot.getValue<MenuItem>()
                            if (resp != null) {
                                grandTotal += resp.total.toString().toFloat()
                            }
                        }
                    }


                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Toast.makeText(this@MenuActivity, "Some thing went wrong", Toast.LENGTH_SHORT).show()
                }
            }
            chRef.addValueEventListener(postListener)

            val cartRef = database.child(userId).child("cart").child(menu.id.toString())
            val cartListener = object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    val responses = dataSnapshot.getValue<Menu>()
                    if (responses != null) {
                        binding.cartCountItem.text = responses.qty.toString()
                        grandTotal += responses.total.toString().toFloat()
                    }else{
                        binding.cartCountItem.text = "0.0"
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Toast.makeText(this@MenuActivity, "Some thing went wrong", Toast.LENGTH_SHORT).show()
                }
            }
            cartRef.addValueEventListener(cartListener)

            val cartAllRef = database.child(userId).child("cart")
            val cartListenerAll = object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    if(dataSnapshot.exists()){
                        var cost: Float = 0F

                        for (postSnapshot in dataSnapshot.children) {
                            val resp = postSnapshot.getValue<Menu>()
                            if (resp != null) {
                                grandTotal = resp.total.toString().toFloat()
                            }
                        }
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Toast.makeText(this@MenuActivity, "Some thing went wrong", Toast.LENGTH_SHORT).show()
                }
            }

            cartAllRef.addValueEventListener(cartListenerAll)

            binding.totalCart.text = grandTotal.toString()
        }
    }

    private fun initCheckBox(){
        val layout = binding.layoutCheckbox as ViewGroup
        val layoutRecommended = binding.checkboxRecommended as ViewGroup
        val sv = ScrollView(this)
        val svr = ScrollView(this)
        val ll = LinearLayout(this)
        val llr = LinearLayout(this)
        ll.orientation = LinearLayout.VERTICAL
        sv.addView(ll)
        svr.addView(llr)
        layout.addView(sv)
        layoutRecommended.addView(svr)
        GlobalScope.launch {
            var items = (application as App).repository.menuItems(menu.id.toString())
            if(items.isNotEmpty()){

                runOnUiThread {

                    items.forEach {
                        val ch = CheckBox(this@MenuActivity)
                        if(it.main?.equals("1") == true){
                            ch.text = it.product_name+ " "+ " ("+it.qty+") " +it.price+it.currency
                            ch.isChecked = true
                            ch.isEnabled = false
                            ch.id = it.id.toInt()
                            ll.addView(ch)
                        }else{
                            ch.text = it.product_name+ " "+ " (1) " +it.price+it.currency
                            ch.setOnClickListener {
                                checkBoxOnClick(it)
                            }
                            ch.id = it.id.toInt()
                            val preference = application?.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE)
                            if (preference != null) {
                                val userId = preference.getString(Constants.USER_ID, "false").toString()
                                val chRef = database.child(userId).child("cart_items").child(it.id.toString())
                                val postListener = object : ValueEventListener {
                                    @SuppressLint("SetTextI18n")
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        // Get Post object and use the values to update the UI
                                        val response = dataSnapshot.getValue<MenuItem>()
                                        if (response != null) {
                                            ch.text = response.product_name+ " "+ " ("+response.qty+") " +response.total+response.currency
                                            ch.isChecked = true
                                        }else{
                                            ch.isChecked = false
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        // Getting Post failed, log a message
                                        Toast.makeText(this@MenuActivity, "Some thing went wrong", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                chRef.addValueEventListener(postListener)
                            }
                            llr.addView(ch)
                        }

                    }
                }
            }
        }
    }

    private fun initCartActions(){
        binding.cartIncrease.setOnClickListener {
            if(binding.itemId.text.toString().isNotEmpty()){
                GlobalScope.launch {
                    val menuItem = (application as App).repository.getMenuItem(binding.itemId.text.toString())
                    runOnUiThread {
                        val preference = application?.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE)
                        if (preference != null) {
                            val userid = preference.getString(Constants.USER_ID, "false").toString()
                            if(userid.isNotEmpty()){
                                menuItem.qty = (binding.cartCount.text.toString().toInt() + 1).toString()
                                menuItem.total = (menuItem.qty.toString().toFloat()* menuItem.price.toString().toFloat()).toString()
                                database.child(userid).child("cart_items").child(menuItem.id.toString()).setValue(menuItem)
                                    .addOnSuccessListener {
                                      //  binding.cartCount.text = (binding.cartCount.text.toString().toInt() + 1).toString()
                                        if(currentView != null){
                                            currentView.isChecked = true
                                        }
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this@MenuActivity, "Some thing went wrong", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }

                    }
                }
            }
        }
        binding.cartDecrease.setOnClickListener {
            it.isActivated = false
            if(binding.itemId.text.toString().isNotEmpty()){
                GlobalScope.launch {
                    val menuItem = (application as App).repository.getMenuItem(binding.itemId.text.toString())
                    runOnUiThread {
                        val preference = application?.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE)
                        if (preference != null) {
                            val userid = preference.getString(Constants.USER_ID, "false").toString()
                            if(userid.isNotEmpty()){
                                menuItem.qty = (binding.cartCount.text.toString().toInt() - 1).toString()
                                if(menuItem.qty.toString().equals("-1")){
                                    database.child(userid).child("cart_items").child(menuItem.id.toString()).setValue(null)
                                        .addOnSuccessListener {
                                            binding.cartCount.text = "0"
                                            if(currentView != null){
                                                currentView.isChecked = false
                                            }
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(this@MenuActivity, "Some thing went wrong", Toast.LENGTH_SHORT).show()
                                        }
                                }else if(menuItem.qty.toString().equals("0")){
                                    database.child(userid).child("cart_items").child(menuItem.id.toString()).setValue(null)
                                        .addOnSuccessListener {
                                            binding.cartCount.text = "0"
                                            if(currentView != null){
                                                currentView.isChecked = false
                                            }
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(this@MenuActivity, "Some thing went wrong", Toast.LENGTH_SHORT).show()
                                        }
                                }else{
                                    menuItem.total = (menuItem.qty.toString().toFloat()* menuItem.price.toString().toFloat()).toString()
                                    database.child(userid).child("cart_items").child(menuItem.id.toString()).setValue(menuItem)
                                        .addOnSuccessListener {
                                           // binding.cartCount.text = (binding.cartCount.text.toString().toInt() - 1).toString()
                                            if(currentView != null){
                                                currentView.isChecked = true
                                            }
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(this@MenuActivity, "Some thing went wrong", Toast.LENGTH_SHORT).show()
                                        }
                                }

                            }
                        }

                    }
                }
            }
        }
    }

    private fun checkBoxOnClick(view: View){
        GlobalScope.launch {
            val menuItem = (application as App).repository.getMenuItem(view.id.toString())
            if(menuItem != null){
                runOnUiThread {
                    Tools.displayImageBusiness(this@MenuActivity,binding.productImage,menuItem.product_image)
                    binding.productName.text = menuItem.product_name
                    binding.productPrice.text = menuItem.price+menuItem.currency
                    binding.itemId.setText(menuItem.id.toString())
                    binding.itemPrice.setText(menuItem.price.toString())
                    currentView = view as CheckBox

                    val preference = application?.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE)
                    if (preference != null) {
                        val userId = preference.getString(Constants.USER_ID, "false").toString()
                        val chRef =  database.child(userId).child("cart_items").child(menuItem.id.toString())
                        val postListener = object : ValueEventListener {
                            @SuppressLint("SetTextI18n")
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                // Get Post object and use the values to update the UI
                                val response = dataSnapshot.getValue<MenuItem>()
                                if (response != null) {
                                    binding.cartCount.text = response.qty
                                    view.isChecked = true
                                }else{
                                    binding.cartCount.text = "00"
                                    view.isChecked = false
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // Getting Post failed, log a message
                                Toast.makeText(this@MenuActivity, "Some thing went wrong", Toast.LENGTH_SHORT).show()
                            }
                        }

                        chRef.addValueEventListener(postListener)

                    }

                }

            }
        }
        toggleSlideUp()
    }

    companion object {
        @JvmStatic
        fun createMenuIntent(context: Context, menu: Menu?): Intent {
            return Intent().setClass(context, MenuActivity::class.java)
                .putExtra(Constants.INTENT_PARAM_1, menu)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initMenu(){
        binding.serviceName.text  = menu.name
        binding.serviceCategory.text = menu.cousin_name
        binding.description.text = menu.description
        binding.servicePrice.text = myMoney(menu.price.toString())+menu.currency
    }

    private fun onCartAddClicked(postRef: DatabaseReference) {
        // ...
        postRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val p = mutableData.getValue(MenuItem::class.java)
                        ?: return Transaction.success(mutableData)

                if (p.main?.equals("1") == true) {
                    p.qty = (p.qty.toString().toFloat() + ("1").toString().toFloat()).toString()
                    p.total = (p.qty.toString().toFloat() * (p.price).toString().toFloat()).toString()
                }else{
                    p.qty = (p.qty.toString().toFloat() + ("1").toString().toFloat()).toString()
                    p.total = (p.qty.toString().toFloat() * (p.price).toString().toFloat()).toString()
                }

                // Set value and report transaction success
                mutableData.value = p
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                    databaseError: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
            ) {
                // Transaction completed
                Log.d("KELLY", "postTransaction:onComplete:" + databaseError?.toString())
            }
        })
    }

}
