package com.yangwenhao.photogallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class PhotoGalleryViewModel : ViewModel() {

    private val flickrFetchr: FlickrFetchr = FlickrFetchr()
    val galleryItemLiveData: LiveData<List<GalleryItem>>
    private val mutableSearchTerm = MutableLiveData<String>()

    init {
        mutableSearchTerm.value = "planets"
        galleryItemLiveData = Transformations.switchMap(mutableSearchTerm) { searchTerm ->
            flickrFetchr.searchPhotos(searchTerm)
        }
    }

    fun fetchPhotos(query: String="") {
        mutableSearchTerm.value = query
    }

    override fun onCleared() {
        super.onCleared()
        flickrFetchr.cancelRequestInFlight()
    }
}