package com.user.tayari

import android.os.Bundle
import android.view.View
import user.tayari.R
import user.tayari.databinding.ActivitySettingsBinding


class SettingsActivity : BaseActivity() {



    lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        var view: View = binding.root
        setContentView(view)
        tools.setSystemBarLight(this)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.title = getString(R.string.app_name)
    }
}

