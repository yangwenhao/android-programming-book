package com.yangwenhao.criminalintent

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment

private const val ARG_PHOTO_URI = "photo_uri"

class PhotoFragment : DialogFragment() {

    private lateinit var imageView: ImageView
    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photoUri = arguments?.getParcelable(ARG_PHOTO_URI)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_photo, container, false)
        imageView = view.findViewById(R.id.image_view)
        imageView.setImageURI(photoUri)
        return view
    }

    companion object {
        fun newInstance(photoUri: Uri): PhotoFragment {
            val args = Bundle().apply {
                putParcelable(ARG_PHOTO_URI, photoUri)
            }
            return PhotoFragment().apply {
                arguments = args
            }
        }
    }
}