package com.lockminds.tayari

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.annotation.RequiresApi
import com.lockminds.tayari.constants.Constants
import com.lockminds.tayari.databinding.ActivityServiceDetailsBinding
import com.mancj.slideup.SlideUp
import com.mancj.slideup.SlideUpBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ServiceDetailsActivity : BaseActivity() {

    lateinit var binding: ActivityServiceDetailsBinding
    private val slideUp: SlideUp? = null
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceDetailsBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        initComponents()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initComponents() {
        val bundle: Bundle? = intent.extras
        binding.toolbar.navigationIcon = getDrawable(R.drawable.ic_back_white)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = null
        Tools.setSystemBarColor(this, R.color.colorPrimaryVariant)
        Tools.displayImageBusiness(
            applicationContext,
            binding.image,
            bundle?.getString(Constants.IMAGE_URL)
        )
        initService()


        val slideUp = SlideUpBuilder(binding.slideView)
            .withListeners(object : SlideUp.Listener.Events {
                override fun onSlide(percent: Float) {

                }

                override fun onVisibilityChanged(visibility: Int) {
                    if (visibility == View.GONE) {

                    }
                }
            })
            .withStartGravity(Gravity.TOP)
            .withLoggingEnabled(true)
            .withStartState(SlideUp.State.HIDDEN)
            .withSlideFromOtherView(binding.parent)
            .build()

        binding.buyNow.setOnClickListener {
            slideUp.show()
        }

    }

    private fun configureBackdrop() {

    }

    private fun initService(){
        val bundle: Bundle? = intent.extras
        val key = bundle?.getString(Constants.DATA_KEY)
        GlobalScope.launch {
            val restaurant = key?.let { (application as App).repository.getRestaurant(it) }!!
            binding.serviceName.text  = restaurant.business_name
            binding.serviceCategory.text = "Brazilian Ugali"
            binding.servicePrice.text = "TZS 7,300"
        }
    }

}