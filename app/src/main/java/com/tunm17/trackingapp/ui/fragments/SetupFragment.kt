package com.tunm17.trackingapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tunm17.trackingapp.R
import com.tunm17.trackingapp.databinding.FragmentSetupBinding
import com.tunm17.trackingapp.databinding.FragmentTrackingBinding
import com.tunm17.trackingapp.ui.viewmodels.MainViewModel

class SetupFragment : Fragment() {

    private lateinit var binding: FragmentSetupBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.startBtn.setOnClickListener {
            findNavController().navigate(R.id.action_setupFragment_to_runFragment)
        }
    }
}