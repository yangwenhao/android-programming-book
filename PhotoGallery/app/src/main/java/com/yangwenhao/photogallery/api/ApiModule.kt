package com.yangwenhao.photogallery.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun provideFlickrApi() : FlickrApi {
        val retrofit = Retrofit.Builder().baseUrl("https://www.api.flickr.com/").addConverterFactory(
            GsonConverterFactory.create()).build()
        return retrofit.create(FlickrApi::class.java)
    }
}