package com.example.trashapp.view.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.trashapp.R
import com.example.trashapp.databinding.AdminReportItemBinding
import com.example.trashapp.databinding.MyReportItemBinding
import com.example.trashapp.network.model.MyReportList
import com.example.trashapp.network.model.ReportTrashCan
import com.example.trashapp.network.model.UserReportList
import com.example.trashapp.view.SharedPreferencesManager
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

            binding.myReportTrashcanId.text = item.detailAddress
            if(item.reportCategory == "0"){
                binding.myReportCategory.text = "위치가 다름"
            } else{
                binding.myReportCategory.text = "위치에 없음"
            }

            if(item.modifyStatus){
                binding.myReportModify.text = "처리 완료"
                binding.myReportModify.setTextColor(ContextCompat.getColor(context, R.color.green)) // 초록색
            }
            else{
                binding.myReportModify.text = "처리 중"
                binding.myReportModify.setTextColor(ContextCompat.getColor(context, R.color.red)) // 초록색

            }

            // 삭제 버튼 클릭 시
            binding.myReportDeleteBtn.setOnClickListener {
                val dialog = Dialog(context)
                dialog.setContentView(R.layout.my_report_dialog)
                dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

                val confirmButton : Button = dialog.findViewById(R.id.myReportDialogConfirm)
                val cancelButton : Button = dialog.findViewById(R.id.myReportDialogCancel)

                confirmButton.setOnClickListener {
                    Log.d("신고 삭제 버튼 클릭","${myReportList[position]}")
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val reportTrashCan = ReportTrashCan(item.trashcanId.toString(), item.reportCategory)
                        viewModel.deleteReportTrashcan(reportTrashCan, SharedPreferencesManager.getToken(context)!!)
                    }
                    Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }

                cancelButton.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }

            // 삭제 버튼 애니메이션
            binding.myReportDeleteBtn.setOnTouchListener{v, event ->
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