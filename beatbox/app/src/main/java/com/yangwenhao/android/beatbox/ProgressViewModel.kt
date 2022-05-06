package com.yangwenhao.android.beatbox

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

class ProgressViewModel(private val beatBox: BeatBox) : BaseObservable() {

    @get:Bindable
    var progress: Int = 0

    @get:Bindable
    val progressText: String
        get() = "Playback Speed ${progress}%"

    fun onProgressChanged(progress: Int) {
        this.progress = progress

    }
}