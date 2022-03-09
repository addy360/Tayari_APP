package com.user.tayari.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.user.tayari.BaseActivity
import com.user.tayari.R
import com.user.tayari.constants.Constants
import com.user.tayari.databinding.ActivityBrowserBinding
import com.user.tayari.model.Order
import com.user.tayari.model.Parameters

class BrowserActivity : BaseActivity() {
    lateinit var binding: ActivityBrowserBinding
    lateinit var parameters: Parameters
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBrowserBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        parameters = intent.getParcelableExtra(Constants.INTENT_PARAM_1)!!
        binding.toolbar.navigationIcon = getDrawable(R.drawable.ic_baseline_arrow_back_24)
        binding.toolbar.title =""
        setSupportActionBar(binding.toolbar)
        initStatusBar()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        startBrowser()
    }

    fun startBrowser(){
        binding.webview.loadUrl(parameters.param_1.toString())
    }

    companion object{
        @JvmStatic
        fun createOrderIntent(context: Context, parameters: Parameters?): Intent {
            return Intent().setClass(context,BrowserActivity::class.java).
            putExtra(Constants.INTENT_PARAM_1,parameters)
        }
    }

}