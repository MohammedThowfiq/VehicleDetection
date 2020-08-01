package com.mohammedthowfiq.vehicledetection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mohammedthowfiq.vehicledetection.R
import com.mohammedthowfiq.vehicledetection.model.ImageModel
import com.squareup.picasso.Picasso

class HistoryAdapter(val context: Context, val items:ArrayList<ImageModel>):RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {


    class HistoryViewHolder(view: View):RecyclerView.ViewHolder(view){

        val image:ImageView = view.findViewById(R.id.imgFromFirebase)
        val textDate:TextView = view.findViewById(R.id.txtDateFromFirebase)
        val textTime:TextView = view.findViewById(R.id.txtTimeFromFirebase)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {


        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_history_single_row,parent,false)

        return HistoryViewHolder(view)
    }

    override fun getItemCount(): Int {

        return items.size

    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {

        val ImageData = items[position]
        Picasso.get().load(ImageData.imageUrl).error(R.drawable.ic_dummy_image).into(holder.image)
        holder.textDate.text = ImageData.date
        holder.textTime.text = ImageData.time

    }


}