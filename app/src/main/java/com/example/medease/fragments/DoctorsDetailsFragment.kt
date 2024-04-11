package com.example.medease.fragments

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.example.medease.Constants
import com.example.medease.adapters.PrescriptionAdapter
import com.example.medease.models.DoctorsModel
import com.example.medease.models.PrescriptionModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import ecom.example.medease.R
import ecom.example.medease.databinding.FragmentDoctorsDetailsBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class DoctorsDetailsFragment : Fragment() {
    lateinit var binding: FragmentDoctorsDetailsBinding
    var DoctorId = ""
    var collectionName = Constants.Doctors
    var Doctorimage=""
    val db = Firebase.firestore
    var mAuth = Firebase.auth
    lateinit var prescriptionAdapter: PrescriptionAdapter
    private var storageRef = FirebaseStorage.getInstance()
    var uriContent : Uri?= null
    var downloadUri: Uri?=null
    var imgProfile: ImageView?=null
    var uriFilePath : String ?= null
    var speId=""
    var doctorAuthid=""
    var CustomerAuthid=""
    var progressBar:ProgressBar?=null
    private val TAG = DoctorsDetailsFragment::class.java.canonicalName
    private var mediaPermission = if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.S)
        Manifest.permission.READ_EXTERNAL_STORAGE
    else{
        Manifest.permission.READ_MEDIA_IMAGES
    }

    private var getImagePermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if(it)
            launchCropImage()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                mediaPermission
            ) != PackageManager.PERMISSION_GRANTED ) {
            getImagePermission.launch(mediaPermission)
        } else{
            launchCropImage()
        }
    }

    fun launchCropImage(){
        cropImage.launch(
            CropImageContractOptions(
                uri = null,
                cropImageOptions = CropImageOptions(
                    imageSourceIncludeCamera = false,
                    imageSourceIncludeGallery = true,
                ),
            )
        )
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // Use the returned uri.
            uriContent = result.uriContent
            uriFilePath = result.getUriFilePath(requireContext()) // optional usage
            binding.imgCustomer.setImageURI(uriContent)
        } else {
            // An error occurred.
            val exception = result.error
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressBar = ProgressBar(requireContext(), null, android.R.attr.progressBarStyleSmall)
        progressBar?.visibility=View.GONE

        arguments?.let {
            DoctorId = it.getString(Constants.id,"") ?:""
        }
        Log.e("subcategoryId"," ${DoctorId}")

        val currentUser = mAuth.currentUser
        CustomerAuthid = currentUser?.uid.toString()
        db.collection(collectionName).document(DoctorId)
            .addSnapshotListener{snapshots,e->
                if (e != null){
                    return@addSnapshotListener
                }
                var model = snapshots?.toObject(DoctorsModel::class.java)
                doctorAuthid=model?.docAuthId.toString()
                binding.tvName.setText(model?.docName)
                binding.tvQualification.setText(model?.docQualificatrion)
                binding.tvExperience.setText("${ model?.docExperience } yrs")
                binding.tvSpecialization.setText(model?.docSpecialization)

              println("DoctorAuthId: ${model?.docAuthId}")

                Doctorimage=model?.docImage.toString()
                Glide
                    .with(requireContext())
                    .load(model?.docImage)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(binding.imgDoctor)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentDoctorsDetailsBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar=binding.pbar
        binding.customizechkbox.setOnCheckedChangeListener { buttonView, isChecked ->

            if (binding.customizechkbox.isChecked == false) {
                binding.llPrescription.visibility = View.GONE

            } else {
                binding.llPrescription.visibility = View.VISIBLE
            }



        }
        var cal = Calendar.getInstance()
        binding.tvRequestDate.text =SimpleDateFormat("dd-MMM-yyyy",Locale.US).format(cal.time)
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd-MMM-yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            binding.tvRequestDate.text = sdf.format(cal.time)

        }
        binding.tvRequestDate.setOnClickListener {
            Toast.makeText(requireContext(), "datepicker clicked", Toast.LENGTH_SHORT).show()
            DatePickerDialog(requireContext(), dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }
        binding.imgCustomer.setOnClickListener {
            checkPermissions()

        }
        binding.btnRequestPrescription.setOnClickListener {
            progressBar?.visibility=View.VISIBLE
            if (binding.edtProblemtext.text.isNullOrEmpty()){
                binding.tilProblemText.error="Enter Problem to request Permissiomn"
                progressBar?.visibility=View.GONE

            }
            else if(uriContent != null) {
                val ref = storageRef.reference.child(Calendar.getInstance().timeInMillis.toString())
                var uploadTask = uriContent?.let { it1 -> ref.putFile(it1) }

                uploadTask?.continueWithTask { task ->
                    System.out.println("in task $task")
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    ref.downloadUrl
                }?.addOnCompleteListener { task ->
                    progressBar?.visibility=View.VISIBLE

                    System.out.println("in on complete listener")
                    if (task.isSuccessful){
                        downloadUri = task.result
                        System.out.println("in on complete listener ${downloadUri.toString()}")
                        binding.imgCustomer.setImageURI(downloadUri)
                        progressBar?.visibility = View.VISIBLE
                        updateData(downloadUri.toString())
                    }
                }
            } else{
                progressBar?.visibility = View.GONE
                updateData()
            }
        }

    }

    private fun updateData(downloadUri: String ?= "") {
        progressBar?.visibility=View.VISIBLE

        var prescriptionModel = PrescriptionModel(
            customerProblems = binding.edtProblemtext.text.toString(),
            customerImage = downloadUri,
            customerAuthId = CustomerAuthid,
            doctorAuthId = doctorAuthid,
            requestDate = binding.tvRequestDate.text.toString()

        )
        Log.e("description", "prescription: ${PrescriptionModel()}")

        db.collection(Constants.prescription).add(prescriptionModel)
            .addOnSuccessListener {
                progressBar?.visibility = View.GONE
                findNavController().navigate(R.id.myRequestsFragment)
            }.addOnFailureListener{
                Log.e(TAG, "Error $it")
            }
    }

}
