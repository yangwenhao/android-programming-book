package com.yangwenhao.android.beatbox

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import kotlin.math.roundToInt

class ProgressViewModel(private val beatBox: BeatBox) : BaseObservable() {

    var rate: Float = 1.0f

    @get:Bindable
    var progress: Int = 33

    @get:Bindable
    val progressText: String
        get() = "Playback Speed ${rate}"

    fun onProgressChanged(progress: Int) {
        this.progress = progress
        rate = ((0.5f + (2 - 0.5f) * progress / 100) * 10.0f).roundToInt() / 10.0f
        beatBox.setRate(rate)
        notifyChange()
    }

    fun resetProgress() {
        rate = 1.0f
        progress = 33
        notifyChange()
    }

}