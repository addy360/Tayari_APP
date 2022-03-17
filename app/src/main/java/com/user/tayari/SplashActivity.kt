package com.user.tayari

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import user.tayari.databinding.ActivitySplashBinding
import java.util.*


class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Start home activity
        binding = ActivitySplashBinding.inflate(layoutInflater);
        setContentView(binding.root);
        val videoPath = "android.resource://$packageName/raw/tayari_splash"

        binding.videoView.setVideoPath(videoPath)
        binding.videoView.setOnCompletionListener {
            Handler().postDelayed({
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                // close splash activity
                finish()
            }, 1000);
        }
        binding.videoView.start();
    }
}