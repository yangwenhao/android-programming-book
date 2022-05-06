package com.yangwenhao.android.beatbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yangwenhao.android.beatbox.databinding.ActivityMainBinding
import com.yangwenhao.android.beatbox.databinding.ListItemSoundBinding

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    //private lateinit var beatBox: BeatBox
    private lateinit var progressViewModel: ProgressViewModel

    private val beatBoxViewModel: BeatBoxViewModel by lazy {
        ViewModelProvider(this).get(BeatBoxViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (beatBoxViewModel.beatBox == null) {
            beatBoxViewModel.beatBox = BeatBox(assets)
        }

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = SoundAdapter(beatBoxViewModel.beatBox.sounds)
        }
        progressViewModel = ProgressViewModel(beatBoxViewModel.beatBox)
        binding.viewModel = progressViewModel
    }

    override fun onDestroy() {
        super.onDestroy()
        //beatBox.release()
    }

    private inner class SoundHolder(private val binding: ListItemSoundBinding): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.soundViewModel = SoundViewModel(beatBoxViewModel.beatBox)
            binding.progressViewModel = progressViewModel
        }

        fun bind(sound: Sound) {
            binding.apply {
                soundViewModel?.sound = sound
                executePendingBindings()
            }
        }
    }

    private inner class SoundAdapter(private val sounds: List<Sound>) : RecyclerView.Adapter<SoundHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundHolder {
            val binding = DataBindingUtil.inflate<ListItemSoundBinding>(layoutInflater, R.layout.list_item_sound, parent,false)
            return SoundHolder(binding)
        }

        override fun onBindViewHolder(holder: SoundHolder, position: Int) {
            val sound = sounds[position]
            holder.bind(sound)
        }

        override fun getItemCount() = sounds.size
    }

}