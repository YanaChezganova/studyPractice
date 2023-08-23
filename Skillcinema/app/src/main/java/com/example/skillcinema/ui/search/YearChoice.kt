package com.example.skillcinema.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.skillcinema.R
import com.example.skillcinema.databinding.YearChoiceBinding
import com.example.skillcinema.ui.data.Constants
import com.example.skillcinema.ui.data.Constants.YEAR_SINCE
import com.example.skillcinema.ui.data.Constants.YEAR_UNTIL
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class YearChoice : Fragment() {
    private var _binding: YearChoiceBinding? = null
    private val binding get() = _binding!!
    private var bundle = Bundle()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = YearChoiceBinding.inflate(inflater, container, false)
        binding.layoutWithHeader.setHeader("Период")
        binding.layoutWithHeader.binding.buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_global_searchSettings)
        }
        binding.tableLayoutSince.setPeriod(1998)
        binding.tableLayoutSince.binding.buttonForward.setOnClickListener {
            binding.tableLayoutSince.shiftToFuture()
        }
        binding.tableLayoutSince.binding.buttonBack.setOnClickListener {
            binding.tableLayoutSince.shiftToPast()
        }
        binding.tableLayoutUntil.setPeriod(2013)
        binding.tableLayoutUntil.binding.buttonForward.setOnClickListener {
            binding.tableLayoutUntil.shiftToFuture()
        }
        binding.tableLayoutUntil.binding.buttonBack.setOnClickListener {
            binding.tableLayoutUntil.shiftToPast()
        }
        binding.tableLayoutSince.listOfView.onEach {
            binding.tableLayoutSince.onClick(it)
        }
        binding.tableLayoutUntil.listOfView.onEach {
            binding.tableLayoutUntil.onClick(it)
        }
        binding.buttonMakeChoice.setOnClickListener {
            val resultSince = binding.tableLayoutSince.onMakeChoice()
            val resultUntil = binding.tableLayoutUntil.onMakeChoice()
            bundle = Bundle().apply {
                putString(YEAR_SINCE, resultSince)
                putString(YEAR_UNTIL, resultUntil)
            }
            findNavController().navigate(R.id.action_global_searchSettings, bundle)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}