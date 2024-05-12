package com.example.trashapp.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trashapp.R
import com.example.trashapp.databinding.FragmentMyReportBinding
import com.example.trashapp.view.adapter.MyReportAdapter
import com.example.trashapp.viewmodel.MyReportViewModel

class MyReportFragment : Fragment() {

    private lateinit var binding : FragmentMyReportBinding

    private val viewModel : MyReportViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyReportBinding.inflate(inflater,container,false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 애니메이션
        val scaleDown = AnimationUtils.loadAnimation(context, R.anim.base_scale_down)
        val scaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up)

        // 뒤로가기 버튼 클릭 시
        binding.myReportBackBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 뒤로가기 버튼 애니메이션
        binding.myReportBackBtn.setOnTouchListener{v, event ->
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

        // 내 신고 목록 RecyclerView
        viewModel.myReportList.observe(viewLifecycleOwner){ myReportList ->
            if(myReportList.isEmpty()) {
                binding.myReportRV.visibility = View.GONE
                binding.reportView.visibility = View.VISIBLE
            }
            else{
                binding.myReportRV.visibility = View.VISIBLE
                binding.reportView.visibility = View.GONE
                binding.myReportRV.adapter = MyReportAdapter(myReportList, viewModel)
                binding.myReportRV.layoutManager = LinearLayoutManager(context)
            }
        }

    }
}