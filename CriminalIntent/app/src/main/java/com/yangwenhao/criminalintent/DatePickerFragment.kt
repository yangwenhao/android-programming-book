package com.yangwenhao.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

private const val ARG_DATE = "date"
private const val REQUEST_KEY = "DialogDate"

class DatePickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date = arguments?.getSerializable(ARG_DATE) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        val dateListener = DatePickerDialog.OnDateSetListener {
            _, year, month, day ->
            val resultDate: Date = GregorianCalendar(year, month, day).time
            parentFragmentManager.setFragmentResult(REQUEST_KEY, Bundle().apply {
                putSerializable(ARG_DATE, resultDate)
            })
        }
        return DatePickerDialog(requireContext(), dateListener, initialYear, initialMonth, initialDay)
    }

    companion object {
        fun newInstance(date: Date): DatePickerFragment {
            var args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }

            return DatePickerFragment().apply {
                arguments = args
            }
        }
    }
}