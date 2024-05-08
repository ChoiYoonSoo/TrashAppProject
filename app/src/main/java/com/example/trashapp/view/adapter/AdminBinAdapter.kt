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
import androidx.recyclerview.widget.RecyclerView
import com.example.trashapp.R
import com.example.trashapp.databinding.AdminBinItemBinding
import com.example.trashapp.databinding.AdminReportItemBinding
import com.example.trashapp.databinding.FragmentAdminBinBinding
import com.example.trashapp.network.model.UnknownTrashcan
import com.example.trashapp.viewmodel.AdminBinViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class AdminBinAdapter(
    val adminReportList: List<UnknownTrashcan>,
    val viewModel: AdminBinViewModel
) : RecyclerView.Adapter<AdminBinAdapter.Holder>() {

    @SuppressLint("ClickableViewAccessibility")
    inner class Holder(val binding: AdminBinItemBinding, private val context : Context) :
        RecyclerView.ViewHolder(binding.root) {

        // 애니메이션 설정
        val scaleDown = AnimationUtils.loadAnimation(context, R.anim.scale_down)
        val scaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up)

        fun bind(item: UnknownTrashcan, position: Int) {
            val inputString = adminReportList[position].date
            // 날짜 형식으로 변환
            val dateObj = LocalDate.parse(inputString, DateTimeFormatter.ofPattern("yyMMdd"))
            // 출력 형식에 맞게 포맷팅
            val formattedDate = dateObj.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            binding.adminDate.text = formattedDate
            binding.adminAddress.text = adminReportList[position].detailAddress
            if(adminReportList[position].categories == "general") {
                binding.adminCategory.text = "일반"
            } else {
                binding.adminCategory.text = "일반/재활용"
            }

            // 신고 아이템 클릭 이벤트
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

            // 신고 삭제 버튼 클릭 이벤트
            binding.adminDeleteBtn.setOnClickListener {

                val dialog = Dialog(context)
                dialog.setContentView(R.layout.admin_bin_dialog)
                dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

                val confirmButton : Button = dialog.findViewById(R.id.adminDialogConfirm)
                val cancelButton : Button = dialog.findViewById(R.id.adminDialogCancel)

                confirmButton.setOnClickListener {
                    Log.d("신고 삭제 버튼 클릭","${adminReportList[position]}")
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        viewModel.deleteAdminUnknownList(adminReportList[position])
                    }
                    Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }

                cancelButton.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }

            // 신고 삭제 버튼 터치 이벤트
            binding.adminDeleteBtn.setOnTouchListener{v, event ->
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
                Log.d("신고 아이템 클릭","${adminReportList[position]}")
                viewModel.trachcanId = adminReportList[position].unknown_trashcan_id.toString()
                viewModel.userId = adminReportList[position].userId.toString()
                viewModel.location = adminReportList[position].latitude.toString() + ", " + adminReportList[position].longitude.toString()
                viewModel.image = adminReportList[position].roadviewImgpath
                viewModel.itemClicked(true)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = AdminBinItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = adminReportList[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int {
        return adminReportList.size
    }

}