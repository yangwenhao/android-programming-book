package com.yangwenhao.criminalintent

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.io.File
import java.lang.String.format
//import java.text.DateFormat
import java.util.*

private const val ARG_CRIME_ID = "crime_id"
private const val DIALOG_DATE = "DialogDate"
private const val DIALOG_PHOTO = "DialogPhoto"
private const val ARG_DATE = "date"
private const val REQUEST_KEY = "DialogDate"
private const val DATE_FORMAT = "EEE, MMM, dd"
private const val TAG = "CrimeFragment"

class CrimeFragment : Fragment() {

    private lateinit var solvedCheckBox: CheckBox
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button
    private lateinit var dateButton: Button
    private lateinit var callButton: Button
    private lateinit var crime: Crime
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri
    private lateinit var titleField: EditText
    private lateinit var photoButton: ImageButton
    private lateinit var photoView: ImageView

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
            val contactUri: Uri? = it.data?.data
            val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
            val cursor = contactUri?.let { uri ->
                requireActivity().contentResolver.query(
                    uri, queryFields, null, null, null)
            }
            cursor?.use {
                if (it.count != 0) {
                    it.moveToFirst()
                    val suspect = it.getString(0)
                    suspectButton.text = suspect
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean ->
            if (isGranted) {
                var contactID = getContactID()
                dial(contactID)
            } else {
                Toast.makeText(requireContext(), "Calling suspect disabled because you denied the permission", Toast.LENGTH_SHORT).show()
            }
        }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            //requireActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            //updatePhotoView()
        }
    }

    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate...")
        super.onCreate(savedInstanceState)
        crime = Crime()
        photoFile = crimeDetailViewModel.getPhotoFile(crime)
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeDetailViewModel.loadCrime(crimeId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
        reportButton = view.findViewById(R.id.crime_report) as Button
        suspectButton = view.findViewById(R.id.crime_suspect) as Button
        callButton = view.findViewById(R.id.call_suspect) as Button
        photoButton = view.findViewById(R.id.crime_camera)
        photoView = view.findViewById(R.id.crime_photo)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(viewLifecycleOwner) { crime ->
            crime?.let {
                this.crime = crime
                photoFile = crimeDetailViewModel.getPhotoFile(crime)
                photoUri = FileProvider.getUriForFile(requireActivity(), "com.yangwenhao.criminalintent.fileprovider", photoFile)
                updateUI()
                updatePhotoView(photoView.width, photoView.height)
            }
        }
    }

    override fun onResume() {
        super.onResume()

    }

    private fun updateUI() {
        titleField.setText(crime.title)
        dateButton.text = DateFormat.getMediumDateFormat(requireContext()).format(crime.date)
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
        if (suspectButton.text.toString() != getString(R.string.crime_suspect_text)) {
            crime.suspect = suspectButton.text.toString()
        } else if (crime.suspect.isNotEmpty()) {
            suspectButton.text = crime.suspect
        }
        //updatePhotoView()
    }

    private fun updatePhotoView(destWidth: Int, destHeight: Int) {
        if (photoFile.exists()) {
            val bitmap = getScaledBitmap(photoFile.path, destWidth, destHeight)
            photoView.setImageBitmap(bitmap)
        } else {
            photoView.setImageDrawable(null)
        }
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved) getString(R.string.crime_report_solved) else getString(R.string.crime_report_unsolved)
        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()
        val suspect = if (crime.suspect.isBlank()) getString(R.string.crime_report_no_suspect) else crime.suspect
        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)
    }

    override fun onStart() {
        super.onStart()
        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // left blank
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                crime.title = p0.toString()
            }
            override fun afterTextChanged(p0: Editable?) {
                // left blank
            }
        }
        titleField.addTextChangedListener(titleWatcher)

        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }
        }

        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply {
                show(this@CrimeFragment.parentFragmentManager, DIALOG_DATE)
            }
        }

        reportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type= "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }.also {
                intent ->
                val chooserIntent = Intent.createChooser(intent, getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }

        callButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                //ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_CONTACTS), 1)
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            } else {
                var contactID = getContactID()
                dial(contactID)
            }
        }

        suspectButton.apply {
            val pickContactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            setOnClickListener {
                resultLauncher.launch(pickContactIntent)
            }

            val packageManager: PackageManager = requireActivity().packageManager
            val resolvedActivity: ResolveInfo? = packageManager.resolveActivity(pickContactIntent, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) {
                isEnabled = false
            }
        }

        photoButton.apply {
            val packageManager: PackageManager = requireActivity().packageManager
            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity: ResolveInfo? = packageManager.resolveActivity(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) {
                isEnabled = false
            }

            setOnClickListener {
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                val cameraActivities: List<ResolveInfo> = packageManager.queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
                for (cameraActivity in cameraActivities) {
                    requireActivity().grantUriPermission(cameraActivity.activityInfo.packageName, photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
                cameraLauncher.launch(captureImage)
            }
        }

        photoView.setOnClickListener {
            PhotoFragment.newInstance(photoUri).apply {
                show(this@CrimeFragment.parentFragmentManager, DIALOG_PHOTO)
            }
        }

        photoView.viewTreeObserver.addOnGlobalLayoutListener (
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    photoView.viewTreeObserver.removeOnGlobalLayoutListener(this);
                    updatePhotoView(photoView.width, photoView.height)
                }
            });

        parentFragmentManager.setFragmentResultListener(REQUEST_KEY, viewLifecycleOwner) { requestKey, result ->
            if (requestKey == DIALOG_DATE) {
                crime.date = result.getSerializable(ARG_DATE) as Date
                updateUI()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }

    override fun onDetach() {
        super.onDetach()
        //requireActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }

    private fun dial(contactID: String?) {
        val cursor = requireActivity().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +"=?", arrayOf(contactID), null, null)
        cursor?.use {
            if (it.count != 0) {
                it.moveToFirst()
                val phone = it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val number: Uri = Uri.parse("tel:$phone")
                val intent = Intent(Intent.ACTION_DIAL, number)
                startActivity(intent)
            }
        }
    }

    private fun getContactID(): String? {
        if (crime.suspect.isEmpty()) {
            Toast.makeText(context, R.string.no_suspect, Toast.LENGTH_SHORT).show()
        }
        var id: String? = null
        var cursor = requireActivity().contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            arrayOf(ContactsContract.Contacts._ID),
            ContactsContract.Contacts.DISPLAY_NAME + "=?", arrayOf(crime.suspect), null);
        cursor?.use {
            if (it.count != 0) {
                it.moveToFirst()
                id = it.getString(0)
            }
        }
        return id
    }

    companion object {
        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }

}