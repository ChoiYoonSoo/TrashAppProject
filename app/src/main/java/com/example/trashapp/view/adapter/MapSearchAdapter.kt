package com.example.trashapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trashapp.data.MapData
import com.example.trashapp.databinding.ItemMapdataBinding
import com.example.trashapp.network.model.Place
import com.example.trashapp.viewmodel.ApiListViewModel

class MapSearchAdapter(val placeList: List<Place>, val viewModel : ApiListViewModel) :
    RecyclerView.Adapter<MapSearchAdapter.Holder>() {

    inner class Holder(val binding: ItemMapdataBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    viewModel.selectPlace.postValue(placeList[position])
                }
            }
        }
        val name = binding.mapDataName
        val addr = binding.mapDataAddress
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapSearchAdapter.Holder {
        val binding = ItemMapdataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: MapSearchAdapter.Holder, position: Int) {
        holder.name.text = placeList[position].address_name
        if(placeList[position].road_address_name == ""){
            holder.addr.text = "주소 정보 없음"
        }
        else{
            holder.addr.text = placeList[position].road_address_name
        }
    }

    override fun getItemCount(): Int {
        return placeList.size
    }
}