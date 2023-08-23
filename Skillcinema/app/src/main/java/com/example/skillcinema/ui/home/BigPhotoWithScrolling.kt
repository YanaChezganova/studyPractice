package com.example.skillcinema.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import com.bumptech.glide.Glide
import com.example.skillcinema.Application
import com.example.skillcinema.databinding.GalleryBigImageBinding
import com.example.skillcinema.ui.data.CinemaBaseDao
import com.example.skillcinema.ui.data.FilmDB
import com.example.skillcinema.ui.data.Repository
import com.example.skillcinema.ui.models.Photo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.skillcinema.ui.data.Constants.FILM_ID
import com.example.skillcinema.ui.data.Constants.TYPE
import com.example.skillcinema.ui.data.Constants.URL_IMAGE
import com.example.skillcinema.ui.data.Constants.POSITION

@AndroidEntryPoint
class BigPhotoWithScrolling : Fragment() {
    private var _binding: GalleryBigImageBinding? = null
    private val binding get() = _binding!!
    private var imageUrl: String? = ""
    private var type: String = ""
    private var position: Int = 0
    private var filmId: Int = 0
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GalleryBigImageBinding.inflate(inflater, container, false)
        arguments?.let {
            imageUrl = it.getString(URL_IMAGE)
            position = it.getInt(POSITION)
            type = it.getString(TYPE).toString()
            filmId = it.getInt(FILM_ID)
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {

            viewModel.loadPoster(filmId, type, 1)
            var listOfPhoto = listOf<Photo>()
            val scope = CoroutineScope(Dispatchers.Default)
            scope.launch {
                viewModel.poster
                    .collect {
                        listOfPhoto = it
                    }
            }
            delay(600)
            Glide.with(binding.galleryPhoto.context)
                .load(imageUrl)
                .into(binding.galleryPhoto)
            binding.buttonPrevious.setOnClickListener {
                if (position > 0) {
                    imageUrl = listOfPhoto[position - 1].imageUrl
                    Glide.with(binding.galleryPhoto.context)
                        .load(imageUrl)
                        .into(binding.galleryPhoto)
                    position--
                }

            }
            binding.buttonNext.setOnClickListener {
                if (position < listOfPhoto.size - 1) {
                    imageUrl = listOfPhoto[position + 1].imageUrl
                    Glide.with(binding.galleryPhoto.context)
                        .load(imageUrl)
                        .into(binding.galleryPhoto)
                    position++
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
