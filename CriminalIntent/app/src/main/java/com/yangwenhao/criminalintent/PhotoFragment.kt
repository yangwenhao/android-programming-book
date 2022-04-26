package com.yangwenhao.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import java.util.*

private const val ARG_PHOTO_URI = "photo_uri"

class PhotoFragment : DialogFragment() {

    private lateinit var imageView: ImageView
    private lateinit var photoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photoUri = arguments?.getSerializable(ARG_PHOTO_URI) as Uri
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        photoUri = arguments?.getSerializable(ARG_PHOTO_URI) as Uri

        val view = layoutInflater.inflate(R.layout.fragment_photo, container, false)

        AlertDialog.Builder(context).setTitle("自定义布局").setView(layout)
        val dateListener = DatePickerDialog.OnDateSetListener {
                _, year, month, day ->
            val resultDate: Date = GregorianCalendar(year, month, day).time
            this.parentFragmentManager.setFragmentResult(REQUEST_KEY, Bundle().apply {
                putSerializable(ARG_DATE, resultDate)
            })
        }
        return DatePickerDialog(requireContext(), dateListener, initialYear, initialMonth, initialDay)
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