package com.example.trashapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trashapp.data.MapData
import com.example.trashapp.databinding.ItemMapdataBinding

class MapSearchAdapter(val mapDataList: List<MapData>) :
    RecyclerView.Adapter<MapSearchAdapter.Holder>() {

    inner class Holder(val binding: ItemMapdataBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.mapDataName
        val addr = binding.mapDataAddress
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapSearchAdapter.Holder {
        val binding = ItemMapdataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: MapSearchAdapter.Holder, position: Int) {
        holder.name.text = mapDataList[position].name
        holder.addr.text = mapDataList[position].addr
    }

    override fun getItemCount(): Int {
        return mapDataList.size
    }
}