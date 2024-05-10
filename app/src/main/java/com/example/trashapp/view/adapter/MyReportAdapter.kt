package com.example.trashapp.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.trashapp.R
import com.example.trashapp.databinding.AdminReportItemBinding
import com.example.trashapp.databinding.MyReportItemBinding
import com.example.trashapp.network.model.MyReportList
import com.example.trashapp.network.model.UserReportList
import com.example.trashapp.viewmodel.MyReportViewModel

class MyReportAdapter(
    val myReportList: List<MyReportList>,
    val viewModel : MyReportViewModel
) : RecyclerView.Adapter<MyReportAdapter.Holder>() {

    @SuppressLint("ClickableViewAccessibility")
    inner class Holder(val binding: MyReportItemBinding, private val context : Context) :
        RecyclerView.ViewHolder(binding.root) {

        // 애니메이션 설정
        val scaleDown = AnimationUtils.loadAnimation(context, R.anim.scale_down)
        val scaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up)

        fun bind(item: MyReportList, position: Int) {

            binding.myReportTrashcanId.text = item.trashcanId.toString()
            if(item.reportCategory == "0"){
                binding.myReportCategory.text = "지도에 나온 위치와 다른 곳에 있어요"
            } else{
                binding.myReportCategory.text = "지도에 나온 위치에 없어요"
            }

            if(item.modifyStatus){
                binding.myReportModify.text = "처리 완료"
                binding.myReportModify.setTextColor(ContextCompat.getColor(context, R.color.green)) // 초록색
            }
            else{
                binding.myReportModify.text = "처리 중"
                binding.myReportModify.setTextColor(ContextCompat.getColor(context, R.color.red)) // 초록색

            }

            itemView.setOnTouchListener{v, event ->
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

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyReportAdapter.Holder {
        val binding = MyReportItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Holder(binding,parent.context)
    }

    override fun onBindViewHolder(holder: MyReportAdapter.Holder, position: Int) {
        val item = myReportList[position]
        holder.bind(item,position)
    }

    override fun getItemCount(): Int {
        return myReportList.size
    }
}