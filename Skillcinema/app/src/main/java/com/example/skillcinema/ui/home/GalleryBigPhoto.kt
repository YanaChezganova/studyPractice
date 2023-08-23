package com.example.skillcinema.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.GalleryBigImageBinding
import dagger.hilt.android.AndroidEntryPoint
import com.example.skillcinema.ui.data.Constants.URL_IMAGE

@AndroidEntryPoint
class GalleryBigPhoto : Fragment() {
    private var _binding: GalleryBigImageBinding? = null
    private val binding get() = _binding!!
    private var imageUrl: String? = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GalleryBigImageBinding.inflate(inflater, container, false)
        binding.progressBar.visibility = View.VISIBLE
        arguments?.let {
            imageUrl = it.getString(URL_IMAGE)
        }
        Glide.with(binding.galleryPhoto.context)
            .load(imageUrl)
            .into(binding.galleryPhoto)

        return binding.root
    }

    override fun onDestroyView() {

        super.onDestroyView()
        _binding = null

    }

}