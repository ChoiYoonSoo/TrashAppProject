package com.example.trashapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trashapp.data.MapData
import com.example.trashapp.databinding.ItemMapdataBinding
import com.example.trashapp.viewmodel.ApiListViewModel

class MapSearchAdapter(val mapDataList: List<MapData>, val viewModel : ApiListViewModel) :
    RecyclerView.Adapter<MapSearchAdapter.Holder>() {

    inner class Holder(val binding: ItemMapdataBinding) : RecyclerView.ViewHolder(binding.root) {
        // 아이템 클릭 시 뷰모델에 값 변경된 후 -> 맵프래그먼트에서 변동 감지한 후 마커이동 구현해야 함
//        init {
//            binding.root.setOnClickListener {
//                val position = adapterPosition
//                if (position != RecyclerView.NO_POSITION) {
//                    viewModel.selectMapData = mapDataList[position]
//                }
//            }
//        }
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