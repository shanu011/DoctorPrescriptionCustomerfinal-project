package com.example.medease.fragments

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
import com.example.medease.adapters.SpecializationAdapter
import com.example.medease.clickInterface.ClickInterface
import com.example.medease.clickInterface.ClickType

import com.example.medease.models.CategoriesModel
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ecom.example.medease.R
import ecom.example.medease.databinding.FragmentSpecializationBinding

class SpecializationFragment : Fragment() {
    lateinit var binding: FragmentSpecializationBinding
    lateinit var mainActivity: MainActivity
        lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var categoriesAdapter: SpecializationAdapter
    var categoriesList= arrayListOf<CategoriesModel>()
    val db = Firebase.firestore
    var collectionName = Constants.categories
      var imgCandle: ImageView?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivity=activity as MainActivity
        db.collection(collectionName).addSnapshotListener{snapshots,e->
            println("SnapShotListener")

            if (e != null){
                println("SnapShotListener Error: ${e.message}")
                return@addSnapshotListener
            }

            for (snapshot in snapshots!!.documentChanges) {
                val userModel = convertObject( snapshot.document)
                println("SnapShotListener IN LOOP")
                when (snapshot.type) {
                    DocumentChange.Type.ADDED -> {
                        userModel?.let { categoriesList.add(it) }
                        Log.e("", "userModelList ${categoriesList.size}")
                        Log.e("", "userModelListadded ${userModel}")
                    }
                    DocumentChange.Type.MODIFIED -> {
                        userModel?.let {
                            var index = getIndex(userModel)
                            if (index > -1)
                                categoriesList.set(index, it)
                        }
                    }
                    DocumentChange.Type.REMOVED -> {
                        userModel?.let {
                            var index = getIndex(userModel)
                            if (index > -1)
                                categoriesList.removeAt(index)
                        }
                    }
                }
            }
            categoriesAdapter.notifyDataSetChanged()
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentSpecializationBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoriesAdapter= SpecializationAdapter(requireContext(),categoriesList,object : ClickInterface {
            override fun onClick(
                position: Int,
                clickType: ClickType?,
                imageView: ImageView
            ): Boolean {
                when (clickType) {

                    ClickType.ViewClick->{
                        mainActivity.navController.navigate(R.id.doctorListFragment, bundleOf(Constants.specializationId to categoriesList[position].categoryId ))

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
                        .load(Uri.parse(categoriesList[position].categoryImgUri))
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(it1)
                }
            }


        })
//        layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCategory.layoutManager = GridLayoutManager(context,2)
        binding.recyclerCategory.adapter = categoriesAdapter
        Log.e("categoriesList", "onCreate: ${CategoriesModel()}", )


    }

    fun convertObject(snapshot: QueryDocumentSnapshot) : CategoriesModel?{
        val categoriesModel:CategoriesModel? =
            snapshot.toObject(CategoriesModel::class.java)
        categoriesModel?.categoryId = snapshot.id ?: ""
        return categoriesModel
    }

    fun getIndex(categoriesModel:CategoriesModel) : Int{
        var index = -1
        index = categoriesList.indexOfFirst { element ->
            element.categoryId?.equals(categoriesModel.categoryId) == true
        }
        return index
    }
}