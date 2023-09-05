package com.example.photos

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.photos.databinding.FragmentSeePhotoBinding

private const val DATE = "date"
private const val URI = "URI"

class SeePhotoFragment : Fragment() {
    private var dateValue: String? = null
    private var uriValue: String? = null
    private var _binding: FragmentSeePhotoBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSeePhotoBinding.inflate(inflater)
        arguments?.let {
            dateValue = it.getString(DATE)
            uriValue = it.getString(URI)
        }
        binding.dateSeeFragment.text = "Date: $dateValue"
        Glide.with(this)
            .load(Uri.parse(uriValue))
            .into(binding.photoImage)
        return binding.root
    }
}