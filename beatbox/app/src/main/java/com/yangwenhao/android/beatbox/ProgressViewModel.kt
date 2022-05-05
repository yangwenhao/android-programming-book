package com.yangwenhao.android.beatbox

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

class ProgressViewModel : BaseObservable() {

    private var progressValue: Int = 0

    @get:Bindable
    val progress: String
        get() = "Playback Speed ${progressValue}%"

    fun onProgressChanged(progress: Int) {
        progressValue = progress
    }
}