package com.example.medease.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medease.adapters.MyPrescriptionAdapter
import com.example.medease.models.PrescriptionModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import ecom.example.medease.R
import ecom.example.medease.databinding.FragmentMyPrescriptionBinding


class MyPrescriptionFragment : Fragment() {
    val db = Firebase.firestore
    lateinit var binding:FragmentMyPrescriptionBinding
    var auth = Firebase.auth
    lateinit var myPrescriptionAdapter: MyPrescriptionAdapter
    var prescriptionList = ArrayList<PrescriptionModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        db.collection("prescription").get().addOnSuccessListener { document->
            for (document in document.documentChanges){
                var model = document.document.toObject(PrescriptionModel::class.java)
                prescriptionList.add(model)
                println("PrescriptionList:$prescriptionList")
                myPrescriptionAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyPrescriptionBinding.inflate(layoutInflater)
        myPrescriptionAdapter = MyPrescriptionAdapter(prescriptionList)
        binding.recyclerCategory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCategory.adapter = myPrescriptionAdapter
        println("My Prescription ")
        return binding.root
    }

    companion object {

        fun newInstance(param1: String, param2: String) =
            MyPrescriptionFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}