package com.yangwenhao.photogallery.api

import com.google.gson.annotations.SerializedName
import com.yangwenhao.photogallery.GalleryItem

class PhotoResponse {
    @SerializedName("photo")
    lateinit var galleryItems: List<GalleryItem>
}