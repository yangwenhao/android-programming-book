package com.yangwenhao.photogallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class PhotoGalleryViewModel : ViewModel() {

    private val flickrFetchr: FlickrFetchr = FlickrFetchr()
    val galleryItemLiveData: LiveData<List<GalleryItem>>

    init {
        galleryItemLiveData = flickrFetchr.fetchPhotos()
    }

    override fun onCleared() {
        super.onCleared()
        flickrFetchr.cancelRequestInFlight()
    }
}