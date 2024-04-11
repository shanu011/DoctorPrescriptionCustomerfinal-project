package com.example.medease.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.Constants
import com.example.medease.activities.MainActivity
import com.example.medease.adapters.DoctorsAdapter
import com.example.medease.clickInterface.ClickInterface
import com.example.medease.clickInterface.ClickType

import com.example.medease.models.DoctorsModel
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ecom.example.medease.R
import ecom.example.medease.databinding.FragmentDoctorListBinding


class DoctorListFragment : Fragment() {
    lateinit var binding: FragmentDoctorListBinding
    val db = Firebase.firestore
    var collectionName = Constants.Doctors
    var doctorsList= arrayListOf<DoctorsModel>()
    lateinit var doctorsAdapter: DoctorsAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var mainActivity: MainActivity
    var speId=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = activity as MainActivity
        arguments?.let {
            speId = it.getString(Constants.specializationId,"") ?:""
        }
        Log.e("categoryId"," ${speId}")

        db.collection(collectionName).whereEqualTo("doctorSpecId",speId).addSnapshotListener { snapshots, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            for (snapshot in snapshots!!.documentChanges) {
                val userModel = convertObject(snapshot.document)

                when (snapshot.type) {
                    DocumentChange.Type.ADDED -> {
                        userModel?.let { doctorsList.add(it) }
                        Log.e("", "userModelList ${doctorsList}")
                    }

                    DocumentChange.Type.MODIFIED -> {
                        userModel?.let {
                            var index = getIndex(userModel)
                            if (index > -1)
                                doctorsList.set(index, it)
                        }
                    }

                    DocumentChange.Type.REMOVED -> {
                        userModel?.let {
                            var index = getIndex(userModel)
                            if (index > -1)
                                doctorsList.removeAt(index)
                        }
                    }
                }
            }
            doctorsAdapter.notifyDataSetChanged()
        }



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentDoctorListBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doctorsAdapter= DoctorsAdapter(requireContext(),doctorsList,object : ClickInterface {
            override fun onClick(position: Int, clickType: ClickType?, imageView: ImageView): Boolean {
                when (clickType) {

                    ClickType.Details->{
                        mainActivity.navController.navigate(R.id.doctorsDetailsFragment, bundleOf(Constants.id to doctorsList[position].doctorId ))
                    }

                    else -> {}
                }
                return true
            }
            override fun view(position:Int,imageView: ImageView) {

            }

        })
        layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCategory.layoutManager = layoutManager
        binding.recyclerCategory.adapter = doctorsAdapter

    }

    fun convertObject(snapshot: QueryDocumentSnapshot) : DoctorsModel?{
        val doctorModel:DoctorsModel? =
            snapshot.toObject(DoctorsModel::class.java)
        doctorModel?.doctorId = snapshot.id ?: ""
        return doctorModel
    }

    fun getIndex(doctorModel: DoctorsModel) : Int{
        var index = -1
        index = doctorsList.indexOfFirst { element ->
            element.doctorId?.equals(doctorModel.doctorId) == true
        }
        return index
    }

}