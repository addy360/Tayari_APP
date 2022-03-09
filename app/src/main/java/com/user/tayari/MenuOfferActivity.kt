package com.user.tayari

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
import androidx.activity.viewModels
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
import com.user.tayari.CartActivity.Companion.createCartIntent
import com.user.tayari.adapter.CartMenuAdapter
import com.user.tayari.constants.APIURLs
import com.user.tayari.constants.Constants
import com.user.tayari.data.QrRestaurantResponse
import com.user.tayari.databinding.ActivityServiceDetailsBinding
import com.user.tayari.datasource.myMoney
import com.user.tayari.firebase.ui.auth.AuthUiActivity
import com.user.tayari.fragments.RestaurantTabsFragment
import com.user.tayari.model.CartItem
import com.user.tayari.model.CartMenu
import com.user.tayari.model.Menu
import com.user.tayari.model.MenuItem
import com.user.tayari.viewModels.CartItemViewModel
import com.user.tayari.viewModels.CartItemViewModelFactory
import com.user.tayari.viewModels.CartViewModel
import com.user.tayari.viewModels.CartViewModelFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MenuOfferActivity : BaseActivity() {

    lateinit var binding: ActivityServiceDetailsBinding
    lateinit var menu: Menu
    lateinit var currentView: CheckBox

    private  val cartViewModel by viewModels<CartViewModel> {
        CartViewModelFactory((application as App).repository)
    }


    private  val cartViewItemModel by viewModels<CartItemViewModel> {
        CartItemViewModelFactory((application as App).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceDetailsBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        menu = intent.getParcelableExtra(Constants.INTENT_PARAM_1)!!
        menu.price = menu.sale
        menu.image  = menu.sale_image
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
        tools.setSystemBarTransparent(this@MenuOfferActivity)
        tools.displayImageBusiness(applicationContext, binding.image, menu.image.toString())
        initMenu()
        binding.slideClose.setOnClickListener {
            toggleSlideUp()
        }

        binding.cartIncreaseItem.setOnClickListener {
            val menuItem = CartMenu()
                menuItem.type = "menu"
                menuItem.id = menu.id
                menuItem.image = menu.image
                menuItem.price = menu.price
                menuItem.qty = menu.qty
                menuItem.total = (menu.qty.toString().toFloat() * menu.price.toString().toFloat()).toString()
                menuItem.name = menu.name
                menuItem.team_id = menu.team_id
            GlobalScope.launch {
                (application as App).repository.addCartMenu(menuItem)
            }

        }

        binding.cartDecreaseItem.setOnClickListener {
            val menuItem = CartMenu()
            menuItem.type = "menu"
            menuItem.id = menu.id
            menuItem.image = menu.image
            menuItem.price = menu.price
            menuItem.qty = "1"
            menuItem.total = menu.price.toString()
            menuItem.name = menu.name
            menuItem.team_id = menu.team_id
            GlobalScope.launch {
                (application as App).repository.lowerCartMenu(menuItem)
            }
        }

        binding.buyNow.setOnClickListener {
            launchCart()
        }

        binding.cartHolder.setOnClickListener {
            launchCart()
        }

        binding.totalCart.setOnClickListener {
            launchCart()
        }


    }

    private fun initCart(){
        cartViewModel.getMenus(menu.team_id.toString()).observe(this){
            it?.let {
                binding.toolbar.title = "Cart (" + it.size.toString()+") items"
                var total: Float = 0F
                it.forEach { it ->
                    total +=  it.total.toString().toFloat()
                }
                binding.totalCart.text = total.toString()
                binding.cartSize.text = it.size.toString()
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
                        val ch = CheckBox(this@MenuOfferActivity)
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
                            currentView = ch
                            llr.addView(ch)
                            updateItem()
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
                    val menus = (application as App).repository.getMenuItem(binding.itemId.text.toString())
                    val menuItem = CartItem();
                        menuItem.id = menus.id
                        menuItem.menu_id = menu.id.toString()
                        menuItem.product_image = menus.product_image
                        menuItem.price = menus.price
                        menuItem.qty = "1"
                        menuItem.total = menus.price.toString()
                        menuItem.product_name = menus.product_name
                        menuItem.team_id = menus.team_id
                        menuItem?.let {
                            (applicationContext as App).repository.addCartItem(menuItem)
                            runOnUiThread {
                                updateItem()
                            }
                        }

                }
            }
        }

        binding.cartDecrease.setOnClickListener {
            it.isActivated = false
            if(binding.itemId.text.toString().isNotEmpty()){
                GlobalScope.launch {
                    val cartItem = (application as App).repository.getCartItem(binding.itemId.text.toString())
                    cartItem?.let {
                        (applicationContext as App).repository.lowerCartItem(cartItem)
                        runOnUiThread {
                            updateItem()
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
                    tools.displayImageBusiness(this@MenuOfferActivity,binding.productImage,menuItem.product_image.toString())
                    binding.productName.text = menuItem.product_name
                    binding.productPrice.text = menuItem.price+menuItem.currency
                    binding.itemId.setText(menuItem.id.toString())
                    binding.itemPrice.setText(menuItem.price.toString())
                    binding.cartCount.text = menuItem.qty
                    currentView = view as CheckBox
                    updateItem()
                }
            }
        }
        toggleSlideUp()
    }

    companion object {
        @JvmStatic
        fun createMenuOfferIntent(context: Context, menu: Menu?): Intent {
            return Intent().setClass(context, MenuOfferActivity::class.java)
                .putExtra(Constants.INTENT_PARAM_1, menu)

        }
    }

    @SuppressLint("SetTextI18n")
    private fun initMenu(){
        binding.serviceName.text  = menu.name
        binding.serviceCategory.text = menu.cousin_name
        binding.description.text = menu.description
        binding.servicePrice.text = myMoney(menu.price.toString())+menu.currency
        cartViewItemModel.getMenu(menu.id.toString(),"menu").observe(this){
            it?.let {
                binding.servicePrice.text  = it.total.toString()
                binding.cartCountItem.text = it.qty
            }
        }

    }

    private fun launchCart(){
        GlobalScope.launch {
            val restaurant  = (application as App).repository.getRestaurant(menu.team_id.toString())
            runOnUiThread {
                startActivity(createCartIntent(this@MenuOfferActivity,restaurant))
                finish()
            }
        }
    }

    private fun updateItem(){
        GlobalScope.launch {
            val menuItem = (application as App).repository.getCartItem(currentView.id.toString())
            if(menuItem != null){
                runOnUiThread {
                    currentView.text = menuItem.product_name + " ("+menuItem.qty+") "+menuItem.total+menuItem.currency
                    currentView.isChecked = true
                    binding.cartCount.text = menuItem.qty
                }
            }else{
                currentView.isChecked = false
            }
        }
    }


}
