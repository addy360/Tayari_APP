package com.lockminds.tayari

import android.os.Bundle
import android.view.View
import com.lockminds.tayari.databinding.ActivityProfileEditBinding

class ProfileEditActivity : BaseActivity() {

    lateinit var binding: ActivityProfileEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        var view: View = binding.root
        setContentView(view)
        Tools.setSystemBarLight(this)
    }

}