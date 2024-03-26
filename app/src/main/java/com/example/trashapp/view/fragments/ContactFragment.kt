package com.example.trashapp.view.fragments

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.example.trashapp.R
import com.example.trashapp.databinding.FragmentContactBinding

class ContactFragment : Fragment() {

    private lateinit var binding : FragmentContactBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentContactBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.contactBackButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.contactText1.setOnClickListener {
            binding.contactProgressButton1.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.baseGreen))
            binding.contactProgressButton2.setBackgroundColor(Color.parseColor("#EBEBEB"))
            val contactViewVisible1 = view.findViewById<View>(R.id.contactViewContainer1)
            val contactViewVisible2 = view.findViewById<View>(R.id.contactViewContainer2)
            contactViewVisible1.visibility = View.VISIBLE
            contactViewVisible2.visibility = View.GONE

        }

        binding.contactText2.setOnClickListener {
            binding.contactProgressButton1.setBackgroundColor(Color.parseColor("#EBEBEB"))
            binding.contactProgressButton2.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.baseGreen))

            val contactViewVisible1 = view.findViewById<View>(R.id.contactViewContainer1)
            val contactViewVisible2 = view.findViewById<View>(R.id.contactViewContainer2)
            contactViewVisible1.visibility = View.GONE
            contactViewVisible2.visibility = View.VISIBLE

        }

    }

}