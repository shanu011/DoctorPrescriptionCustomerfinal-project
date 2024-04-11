package com.example.medease.fragments

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medease.Constants
import com.example.medease.activities.MainActivity
import com.example.medease.adapters.MyRequestsAdapter
import com.example.medease.clickInterface.ClickInterface
import com.example.medease.clickInterface.ClickType

import com.example.medease.models.PrescriptionModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ecom.example.medease.R
import ecom.example.medease.databinding.FragmentMyRequestsBinding

class MyRequestsFragment : Fragment() {
    lateinit var binding: FragmentMyRequestsBinding
    lateinit var mainActivity: MainActivity
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var categoriesAdapter: MyRequestsAdapter
    var prescriptionModelList= arrayListOf<PrescriptionModel>()
    val db = Firebase.firestore
    var mAuth = Firebase.auth
    var CustomerAuthid = ""

    var collectionName = Constants.prescription


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = mAuth.currentUser
        CustomerAuthid = currentUser?.uid.toString()
        db.collection(Constants.prescription).whereEqualTo("customerAuthId",CustomerAuthid)
            .addSnapshotListener{ snapshot, e->

            if (e != null){
                println("SnapShotListener Error: ${e.message}")
                return@addSnapshotListener
            }
            for (snapshot in snapshot!!.documentChanges) {

                val userModel = convertObject( snapshot.document)
                println("SnapShotListener IN LOOP")
                when (snapshot.type) {
                    DocumentChange.Type.ADDED -> {
                        userModel?.let { prescriptionModelList.add(it) }
                        Log.e("prescriptionModel", "prescriptionList ${prescriptionModelList}")
                        Log.e("prescriptionModel", "prescriptionModel ${userModel}")
                    }
                    DocumentChange.Type.MODIFIED -> {
                        userModel?.let {
                            var index = getIndex(userModel)
                            if (index > -1)
                                prescriptionModelList.set(index, it)
                        }
                    }
                    DocumentChange.Type.REMOVED -> {
                        userModel?.let {
                            var index = getIndex(userModel)
                            if (index > -1)
                                prescriptionModelList.removeAt(index)
                        }
                    }
                }
                categoriesAdapter.notifyDataSetChanged()
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity=activity as MainActivity
        binding= FragmentMyRequestsBinding.inflate(layoutInflater)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoriesAdapter= MyRequestsAdapter(requireContext(),prescriptionModelList,object :
            ClickInterface {
            override fun onClick(
                position: Int,
                clickType: ClickType?,
                imageView: ImageView
            ): Boolean {
                when (clickType) {
                    ClickType.Delete -> {
                        AlertDialog.Builder(requireContext()).apply {
                            setTitle(resources.getString(R.string.delete_alert))
                            setPositiveButton("Yes") { _, _ ->
                                //deleting the particular collection from firestore
                                db.collection(collectionName)
                                    .document(prescriptionModelList[position].prescriptionId ?: "")
                                    .delete()
                            }
                            setNegativeButton("No") { _, _ -> }
                            show()
                        }
                    }

                    ClickType.ViewClick->{
                        mainActivity.navController.navigate(R.id.requestDetailsFragment, bundleOf(Constants.presId to prescriptionModelList[position].prescriptionId ,Constants.CustomerAuthId to prescriptionModelList[position].customerAuthId))

//                        showAddCategoryDialog(position)
                    }
                    else -> {}
                }
                return true
            }

            override fun view(position:Int,imageView: ImageView) {
                imageView?.let { it1 ->
                    Glide
                        .with(requireContext())
                        .load(Uri.parse(prescriptionModelList[position].customerImage))
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(it1)
                }
            }


        })
//        layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCategory.layoutManager = GridLayoutManager(context,2)
        binding.recyclerCategory.adapter = categoriesAdapter
        Log.e("categoriesList", "onCreate: ${PrescriptionModel()}", )

    }

    fun convertObject(snapshot: QueryDocumentSnapshot) : PrescriptionModel?{
        val prescriptionModel:PrescriptionModel? =
            snapshot.toObject(PrescriptionModel::class.java)
        prescriptionModel?.prescriptionId = snapshot.id ?: ""
        return prescriptionModel
    }

    fun getIndex(prescriptionModel:PrescriptionModel) : Int{
        var index = -1
        index = prescriptionModelList.indexOfFirst { element ->
            element.prescriptionId?.equals(prescriptionModel.prescriptionId) == true
        }
        return index
    }
}