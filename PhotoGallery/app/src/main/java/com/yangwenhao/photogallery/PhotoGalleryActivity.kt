package com.yangwenhao.photogallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yangwenhao.photogallery.databinding.ActivityPhotoGalleryBinding

class PhotoGalleryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoGalleryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoGalleryBinding.inflate(layoutInflater);
        setContentView(binding.root)

        val isFragmentContainerEmpty = savedInstanceState == null
        if (isFragmentContainerEmpty) {
            supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, PhotoGalleryFragment.newInstance()).commit()
        }
    }
}