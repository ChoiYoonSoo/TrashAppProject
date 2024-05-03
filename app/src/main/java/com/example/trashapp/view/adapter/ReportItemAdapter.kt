package com.example.trashapp.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.trashapp.R
import com.example.trashapp.data.ReportItemData
import com.example.trashapp.databinding.ItemReportBinding
import com.example.trashapp.network.model.ReportTrashCan
import com.example.trashapp.view.SharedPreferencesManager
import com.example.trashapp.viewmodel.ApiListViewModel

class ReportItemAdapter(val reportList : ArrayList<ReportItemData>, private val viewModel : ApiListViewModel) : RecyclerView.Adapter<ReportItemAdapter.Holder>(){

    inner class Holder(val binding: ItemReportBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {
        val scaleDown = AnimationUtils.loadAnimation(context, R.anim.scale_down)
        val scaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up)

        fun bind(item: ReportItemData, position: Int) {
            binding.reportText.text = item.reportText
            itemView.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        v.startAnimation(scaleDown)
                        true
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        v.startAnimation(scaleUp)
                        v.performClick()
                        true
                    }

                    else -> false
                }
            }

            itemView.setOnClickListener {
                Log.d("신고 버튼 클릭","${item.reportText} 카테고리 : $position, 아이디 : ${viewModel.id}")
                val token = SharedPreferencesManager.getToken(context)
                val report = ReportTrashCan(viewModel.id.toString(), position.toString())
                viewModel.reportApi(report, token!!)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportItemAdapter.Holder {
        val binding = ItemReportBinding.inflate(LayoutInflater.from(parent.context))
        return Holder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: ReportItemAdapter.Holder, position: Int) {
        val item = reportList[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int {
        return reportList.size
    }


}