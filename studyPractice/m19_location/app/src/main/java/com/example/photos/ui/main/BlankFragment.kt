package com.example.photos.ui.main

import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.photos.R
import com.example.photos.databinding.FragmentBlankBinding

class BlankFragment : Fragment() {

    private lateinit var binding: FragmentBlankBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBlankBinding.inflate(layoutInflater)
        val message = TitleText().setGreetingText(getDayInterval())
        binding.title.text = message
        binding.buttonStart.setOnClickListener {
            TitleText().checkText(binding.title.text.toString())
            findNavController().navigate(R.id.action_blankFragment_to_mainFragment)
        }
        return binding.root
    }
    fun getDayInterval(): Int{
        val calendar = Calendar.getInstance()
        val interval = when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 6..11 -> 0
            in 12..16 -> 1
            in 17..22 -> 2
            else -> 3
        }
        return interval
    }


}
class TitleText{
    fun setGreetingText(interval: Int): String {
        val allGreetingTexts = when (interval){
            0 -> "Good morning!"
            1 ->"Good day!"
            2 ->"Good evening!"
            else ->"Good night!"
        }
        return allGreetingTexts
    }
    fun checkText(text: String): Boolean{
        return text.contains("Good")
    }
}
