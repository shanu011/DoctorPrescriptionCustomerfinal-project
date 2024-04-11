package com.example.medease.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.example.medease.Constants

import com.example.medease.models.CustomerRegisterModel
import com.example.medease.models.PrescriptionModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ecom.example.medease.R
import ecom.example.medease.databinding.FragmentRequestDetailsBinding


class RequestDetailsFragment : Fragment() {
    lateinit var binding: FragmentRequestDetailsBinding
    val db = Firebase.firestore
    var mAuth = Firebase.auth
    var prescriptionId=""
    var userauthId=""
    var DoctorAuthid=""
    var progressBar: ProgressBar?=null
    var preObjectModel:PrescriptionModel?=null
    private val TAG = FragmentRequestDetailsBinding::class.java.canonicalName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        progressBar = ProgressBar(requireContext(), null, android.R.attr.progressBarStyleSmall)
        progressBar?.visibility=View.GONE

        arguments?.let {
            prescriptionId= it.getString(Constants.presId,"")?:""
            userauthId=it.getString(Constants.CustomerAuthId,"")?:""
        }
        Log.e("Id","CustomerAuthId: ${userauthId}")
        Log.e("Id","prescriptionId: ${prescriptionId}")

        db.collection(Constants.customers).whereEqualTo("userauthId",userauthId).addSnapshotListener{snapshots,e->
            if (e != null){
                return@addSnapshotListener
            }
            for (snapshot in snapshots!!.documentChanges) {
                val userModel = convertObject( snapshot.document)
                binding.tvName.setText(userModel?.username)
                binding.tvEmail.setText(userModel?.useremail)
            }
        }

        db.collection(Constants.prescription).document(prescriptionId)
            .addSnapshotListener{snapshots,e->
                if (e != null){
                    return@addSnapshotListener
                }

                var model = snapshots?.toObject(PrescriptionModel::class.java)
                Log.e("model", "onCreate:$model ", )
                binding.tvRequestDate.setText(model?.requestDate)
                binding.tvproblem.setText(model?.customerProblems)

                Glide
                    .with(requireContext())
                    .load(model?.customerImage)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(binding.imgCustomerProblem)
                binding.tvSolution.setText(model?.doctorSolution)
                Glide
                    .with(requireContext())
                    .load(model?.doctotrImage)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(binding.imgdoctorSolution)
            }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentRequestDetailsBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar=binding.pbar

    }

    fun convertObject(snapshot: QueryDocumentSnapshot) : CustomerRegisterModel?{
        val categoriesModel:CustomerRegisterModel? =
            snapshot.toObject(CustomerRegisterModel::class.java)
        categoriesModel?.customerId = snapshot.id ?: ""
        return categoriesModel
    }
}