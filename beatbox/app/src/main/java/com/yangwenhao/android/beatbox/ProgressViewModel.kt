package com.yangwenhao.android.beatbox

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

class ProgressViewModel(private val beatBox: BeatBox) : BaseObservable() {

    var rate: Float = 0f

    @get:Bindable
    val progressText: String
        get() = "Playback Speed ${rate}%"

    fun onProgressChanged(progress: Int) {
        rate = 0.5f + (2 - 0.5f) * progress / 100
        beatBox.setRate(rate)
        notifyChange()
    }
}