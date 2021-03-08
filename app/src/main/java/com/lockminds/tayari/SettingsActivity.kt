package com.lockminds.tayari

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.lockminds.tayari.databinding.ActivitySettingsBinding
import com.lockminds.tayari.viewModels.UsersViewModel
import com.lockminds.tayari.viewModels.UsersViewModelFactory


class SettingsActivity : BaseActivity() {

    private val userViewModel: UsersViewModel by viewModels {
        UsersViewModelFactory((application as App).repository)
    }

    lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        var view: View = binding.root
        setContentView(view)
        Tools.setSystemBarLight(this)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.title = getString(R.string.app_name)
        userViewModel.user.observe(this, Observer { user ->
            user?.let {
                binding.toolbar.title = user.name
            }
        })
    }
}

