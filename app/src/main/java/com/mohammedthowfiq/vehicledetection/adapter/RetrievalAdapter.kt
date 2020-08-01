package com.mohammedthowfiq.vehicledetection.adapter

import android.content.Context
import android.text.TextUtils.replace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mohammedthowfiq.vehicledetection.R
import com.mohammedthowfiq.vehicledetection.model.RetrievalModel
import kotlinx.android.synthetic.main.recycler_retrieval_single_row.view.*

class RetrievalAdapter(val context: Context, val items: ArrayList<RetrievalModel>) :
    RecyclerView.Adapter<RetrievalAdapter.RetrievalViewHolder>() {

    class RetrievalViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val colorTextView: TextView = view.findViewById(R.id.txtColor)
        val makeTextView: TextView = view.findViewById(R.id.txtMake)
        val numberPlateTextView: TextView = view.findViewById(R.id.txtNumberPlate)
        val plateConfTextView: TextView = view.findViewById(R.id.txtPlateConf)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RetrievalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_retrieval_single_row, parent, false)

        return RetrievalViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RetrievalViewHolder, position: Int) {

        val retrievalDataObject = items[position]

        var tempColorData = retrievalDataObject.colors.toString().replace("[","]")
        var tempMakeData  = retrievalDataObject.make.toString().replace("[","]")
        var tempNumberPlateData = retrievalDataObject.plate.toString().replace("[","]")
        var tempPlateConfData = retrievalDataObject.plateConfidence.toString().replace("[","]")

        var coloData = tempColorData.replace("]","")
        var makeData = tempMakeData.replace("]","")
        var numberPlateData = tempNumberPlateData.replace("]","")
        var plateConfData = tempPlateConfData.replace("]","")


        holder.colorTextView.text = coloData
        holder.makeTextView.text = makeData
        holder.numberPlateTextView.text = numberPlateData
        holder.plateConfTextView.text = plateConfData





    }


}