package com.example.trashapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trashapp.data.ReportItemData
import com.example.trashapp.databinding.ItemReportBinding

class ReportItemAdapter(val reportList : ArrayList<ReportItemData>) : RecyclerView.Adapter<ReportItemAdapter.Holder>(){

    inner class Holder(val binding: ItemReportBinding) : RecyclerView.ViewHolder(binding.root){
        val reportText = binding.reportText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportItemAdapter.Holder {
        val binding = ItemReportBinding.inflate(LayoutInflater.from(parent.context))
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: ReportItemAdapter.Holder, position: Int) {
        holder.reportText.text = reportList[position].reportText
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

}