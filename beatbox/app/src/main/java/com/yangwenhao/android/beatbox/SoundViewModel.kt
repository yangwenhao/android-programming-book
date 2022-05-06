package com.yangwenhao.android.beatbox

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

class SoundViewModel(private val beatBox: BeatBox) : BaseObservable() {

    var sound: Sound? = null
        set(sound) {
            field = sound
            notifyChange()
        }

    var rate: Float = 1.0f

    @get:Bindable
    var progress: Int = 0

    @get:Bindable
    val title: String?
        get() = sound?.name

    @get:Bindable
    val progressText: String
        get() = "Playback Speed $rate"

    fun onProgressChanged(progress: Int) {
        this.progress = progress
        rate = 0.5f + (2 - 0.5f) * progress / 100
        beatBox.setRate(rate)
        notifyChange()
    }

    fun onButtonClicked() {
        sound?.let {
            beatBox.play(it)
            rate = 1.0f
            progress = 0
            notifyChange()
        }
    }
}