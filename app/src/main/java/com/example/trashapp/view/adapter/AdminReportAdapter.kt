package com.example.trashapp.view.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.trashapp.R
import com.example.trashapp.databinding.AdminReportItemBinding
import com.example.trashapp.network.model.ReportTrashCan
import com.example.trashapp.network.model.UserReportList
import com.example.trashapp.viewmodel.AdminReportViewModel

class AdminReportAdapter(
    val adminReportList: List<UserReportList>,
    val viewModel: AdminReportViewModel
) : RecyclerView.Adapter<AdminReportAdapter.Holder>() {

    @SuppressLint("ClickableViewAccessibility")
    inner class Holder(val binding: AdminReportItemBinding, private val context : Context) :
        RecyclerView.ViewHolder(binding.root) {

        // 애니메이션 설정
        val scaleDown = AnimationUtils.loadAnimation(context, R.anim.scale_down)
        val scaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up)

        fun bind(item: UserReportList, position: Int) {

            binding.adminReportTrashcanId.text = item.trashcanId.toString()
            if(item.reportCategory == "0") {
                binding.adminReportCategory.text = "지도에 나온 위치와 다른 곳에 있어요"
                binding.adminReportCreateBtn.visibility = View.VISIBLE
                binding.adminReportDeleteBtn.visibility = View.GONE
            } else {
                binding.adminReportCategory.text = "지도에 나온 위치에 없어요"
                binding.adminReportCreateBtn.visibility = View.GONE
                binding.adminReportDeleteBtn.visibility = View.VISIBLE
            }

            // 취소 버튼 클릭 이벤트
            binding.adminReportCancelBtn.setOnClickListener {
                viewModel.cancelTrashcanId = item.trashcanId
                viewModel.cancelReportCategory = item.reportCategory
                viewModel.isCancelClicked(true)
            }

            // 삭제 버튼 클릭 이벤트
            binding.adminReportDeleteBtn.setOnClickListener {
                val dialog = Dialog(context)
                dialog.setContentView(R.layout.admin_report_dialog)
                dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

                val confirmButton : Button = dialog.findViewById(R.id.adminReportDialogConfirm)
                val cancelButton : Button = dialog.findViewById(R.id.adminReportDialogCancel)

                confirmButton.setOnClickListener {
                    Log.d("신고 삭제 버튼 클릭","${adminReportList[position]}")
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val reportTrashCan = ReportTrashCan(item.trashcanId.toString(), item.reportCategory)
                        viewModel.deleteTrashcan(reportTrashCan)
                    }
                    Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }

                cancelButton.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }

            // 삭제 버튼 터치 이벤트
            binding.adminReportDeleteBtn.setOnTouchListener{v, event ->
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

            // 등록 버튼 클릭 시
            binding.adminReportCreateBtn.setOnClickListener {
                Log.d("신고 아이템 클릭","${adminReportList[position]}")
                viewModel.trashcanId = item.trashcanId
                viewModel.userId = item.userId
                viewModel.reportId = item.id
                viewModel.itemClicked(true)
            }

            // 수정 버튼 터치 이벤트
            binding.adminReportCreateBtn.setOnTouchListener { v, event ->
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
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminReportAdapter.Holder {
        val binding = AdminReportItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: AdminReportAdapter.Holder, position: Int) {
        val item = adminReportList[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int {
        return adminReportList.size
    }
}