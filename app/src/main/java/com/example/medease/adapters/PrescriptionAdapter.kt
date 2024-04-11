package com.example.medease.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.clickInterface.ClickInterface
import com.example.medease.clickInterface.ClickType
import com.example.medease.models.PrescriptionModel
import ecom.example.medease.databinding.PrescriptionListItemBinding

class PrescriptionAdapter(var context: Context, var arrayList: ArrayList<PrescriptionModel>, var clicklistener: ClickInterface):
    RecyclerView.Adapter<PrescriptionAdapter.ViewHolder>()  {

    class ViewHolder(var binding: PrescriptionListItemBinding): RecyclerView.ViewHolder(binding.root)  {

        fun bindData(
            prescriptionModel: PrescriptionModel,
            position: Int,
            clicklistener: ClickInterface,
            imageView: ImageView
        ){
            binding.prescriptionModel=prescriptionModel
            binding.position=position
            binding.clickListener=clicklistener
            binding.llItemview.setOnClickListener {
                clicklistener.onClick(position, ClickType.ViewClick,imageView)
            }
            binding.btnDetails.setOnClickListener {
                clicklistener.onClick(position, ClickType.Details,imageView)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PrescriptionAdapter.ViewHolder {
        val binding= PrescriptionListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }



    override fun onBindViewHolder(holder: PrescriptionAdapter.ViewHolder, position: Int) {
        holder.apply {

        }
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}