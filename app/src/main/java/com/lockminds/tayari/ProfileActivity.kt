package com.lockminds.tayari

import android.os.Bundle
import android.view.View
import com.lockminds.tayari.databinding.ActivityProfileBinding

class ProfileActivity : BaseActivity() {
    lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        var view: View = binding.root
        setContentView(view)
    }
}