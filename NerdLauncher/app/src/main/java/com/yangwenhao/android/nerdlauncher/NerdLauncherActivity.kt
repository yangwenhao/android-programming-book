package com.yangwenhao.android.nerdlauncher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yangwenhao.android.nerdlauncher.databinding.ActivityNerdLauncherBinding

class NerdLauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNerdLauncherBinding.inflate(layoutInflater);
        setContentView(binding.root)
    }
}