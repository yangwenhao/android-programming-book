package com.yangwenhao.photogallery

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yangwenhao.photogallery.api.FlickrApi
import com.yangwenhao.photogallery.api.FlickrResponse
import com.yangwenhao.photogallery.api.PhotoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val TAG = "FlickrFetchr"

class FlickrFetchr {

    private val flickrApi: FlickrApi
    private lateinit var flickrRequest: Call<FlickrResponse>

    init {
        val retrofit = Retrofit.Builder().baseUrl("https://www.api.flickr.com/").addConverterFactory(
            GsonConverterFactory.create()).build()
        flickrApi = retrofit.create(FlickrApi::class.java)
    }

    fun fetchPhotos(): LiveData<List<GalleryItem>> {
        val responseLiveData: MutableLiveData<List<GalleryItem>> = MutableLiveData()
        flickrRequest = flickrApi.fetchPhotos()

        flickrRequest.enqueue(object : Callback<FlickrResponse> {

            override fun onResponse(call: Call<FlickrResponse>, response: Response<FlickrResponse>) {
                Log.d(TAG, "Response recevied")

                val flickrResponse: FlickrResponse? = response.body()
                val photoResponse: PhotoResponse? = flickrResponse?.photos
                var galleryItems: List<GalleryItem> = photoResponse?.galleryItems ?: mutableListOf()
                galleryItems = galleryItems.filterNot {
                    it.url.isBlank()
                }
                responseLiveData.value = galleryItems
            }

            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {
                if (call.isCanceled) {
                    Log.d(TAG, "Call is cancelled")
                } else {
                    Log.e(TAG, "Failed to fetch photos", t)
                }
            }

        })

        return responseLiveData
    }

    fun cancelRequestInFlight() {
        if (::flickrRequest.isInitialized) {
            flickrRequest.cancel()
        }
    }
}