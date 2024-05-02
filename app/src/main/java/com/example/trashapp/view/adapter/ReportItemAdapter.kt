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
import com.example.trashapp.viewmodel.ApiListViewModel

class ReportItemAdapter(val reportList : ArrayList<ReportItemData>, private val viewModel : ApiListViewModel) : RecyclerView.Adapter<ReportItemAdapter.Holder>(){

    inner class Holder(val binding: ItemReportBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {
        val scaleDown = AnimationUtils.loadAnimation(context, R.anim.scale_down)
        val scaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up)

        fun bind(item: ReportItemData) {
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
                Log.d("신고 버튼 클릭","${item.reportText}")
                viewModel.reportApi(viewModel.id, item.reportText!!)
                Toast.makeText(context, "신고가 완료되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportItemAdapter.Holder {
        val binding = ItemReportBinding.inflate(LayoutInflater.from(parent.context))
        return Holder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: ReportItemAdapter.Holder, position: Int) {
        val item = reportList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return reportList.size
    }


}