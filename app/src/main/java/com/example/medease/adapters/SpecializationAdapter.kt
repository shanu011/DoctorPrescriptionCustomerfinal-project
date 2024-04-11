package com.example.medease.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.medease.clickInterface.ClickInterface
import com.example.medease.clickInterface.ClickType
import com.example.medease.models.CategoriesModel
import ecom.example.medease.R
import ecom.example.medease.databinding.CategoryListItemBinding

class SpecializationAdapter(var context: Context, var arrayList: ArrayList<CategoriesModel>, var clicklistener: ClickInterface):RecyclerView.Adapter<SpecializationAdapter.ViewHolder>() {

    class ViewHolder(var binding: CategoryListItemBinding):RecyclerView.ViewHolder(binding.root) {

//        fun bindData(categoriesModel:CategoriesModel,position: Int,clicklistener: ClickInterface,imageView: ImageView){
//            binding.categoriesModel=categoriesModel
//            binding.position=position
//            binding.clickListener=clicklistener
//
//
////            binding.imgDelete.setOnClickListener {
////                clicklistener.onClick(position, ClickType.Delete)
////            }
////            binding.imgCandle.setOnClickListener {
////                clicklistener.onClick(position,ClickType.img)
////            }
////            binding.tvsubcat.setOnClickListener {
////                clicklistener.onClick(position,ClickType.AddSub)
////            }
//        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SpecializationAdapter.ViewHolder {
        val binding=CategoryListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }



    override fun onBindViewHolder(holder: SpecializationAdapter.ViewHolder, position: Int) {
        holder.apply {
            binding.tvcategory.setText(arrayList[position].categoryName)

            Glide
                .with(context)
                .load(arrayList[position].categoryImgUri)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.imgCandle)

            binding.llcatItemView.setOnClickListener {
                clicklistener.onClick(position, ClickType.ViewClick,binding.imgCandle)
            }
//            binding.imgCandle.setImageURI(Uri.parse(arrayList[position].categoryImgUri))
//            bindData(arrayList[position],position,clicklistener,binding.imgCandle)
//            imgset.setImage(position,binding.imgCandle)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

//    interface imageSetting {
//        fun setImage(position: Int,imageView: ImageView)
//    }
}