package com.example.trashapp.view.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.trashapp.R
import com.example.trashapp.databinding.FragmentUnsubBinding

class UnsubFragment : Fragment() {

    private lateinit var binding : FragmentUnsubBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUnsubBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.unSubBackButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.unSub.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.unsub_dialog)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            val button : Button = dialog.findViewById(R.id.unsubConfirm)
            button.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()

        }
    }
}