package com.example.medease.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medease.models.PrescriptionModel
import ecom.example.medease.R
import org.w3c.dom.Text


class MyPrescriptionAdapter (var prescriptionlist:List<PrescriptionModel>):RecyclerView.Adapter<MyPrescriptionAdapter.ViewHolder>(){
    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        var img : ImageView = view.findViewById(R.id.imgCategory)
        var problemName : TextView = view.findViewById(R.id.tvcategory)

    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyPrescriptionAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.prescription_item,parent,false)
        return MyPrescriptionAdapter.ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyPrescriptionAdapter.ViewHolder, position: Int) {
        val item = prescriptionlist[position].customerProblems
        holder.problemName.setText(item)
       Glide.with(holder.itemView)
           .load(prescriptionlist[position].customerImage)
           .into(holder.img)

    }

    override fun getItemCount(): Int {
        return prescriptionlist.size
    }

}