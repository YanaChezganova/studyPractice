package com.example.timercustom.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.timercustom.R
import com.example.timercustom.databinding.MainFragmentBinding
import kotlinx.coroutines.*

class MainFragment : Fragment() {
    private lateinit var binding: MainFragmentBinding

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var customDrawableView: WatchViewGroup
    private var job: Job? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        customDrawableView = binding.watchView

        binding.buttonStart.setOnClickListener {
            job = lifecycleScope.launchWhenCreated {
                it.visibility = View.INVISIBLE
                binding.buttonStop.visibility = View.VISIBLE

                customDrawableView.startTimer()
            }
        }
        binding.buttonStop.setOnClickListener {
            job?.cancel()
            customDrawableView.stopTimer()
            it.visibility = View.INVISIBLE
            binding.buttonStart.visibility = View.VISIBLE
        }
        binding.buttonReset.setOnClickListener {
            job?.cancel()
            customDrawableView.resetTimer()
            binding.buttonStop.visibility = View.INVISIBLE
            binding.buttonStart.visibility = View.VISIBLE
        }
        binding.watchView.viewModel.updatedTimer.observe(
            viewLifecycleOwner,
            Observer<TimerPlaying> { item ->
                binding.textTime.text = resources.getString(
                    R.string.time, item.hour,
                    item.minute, item.second
                )
            })

        return binding.root
    }
}

