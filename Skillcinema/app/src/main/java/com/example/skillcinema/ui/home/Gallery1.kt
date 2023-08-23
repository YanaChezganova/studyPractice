package com.example.skillcinema.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.Application
import com.example.skillcinema.R
import com.example.skillcinema.databinding.GalleryBinding
import com.example.skillcinema.ui.data.CinemaBaseDao
import com.example.skillcinema.ui.data.Repository
import com.example.skillcinema.ui.models.Photo
import com.example.skillcinema.ui.support.PhotoBigAdapterRecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.skillcinema.ui.data.Constants.FILM_ID
import com.example.skillcinema.ui.data.Constants.URL_IMAGE
import com.example.skillcinema.ui.data.Constants.TYPE
import com.example.skillcinema.ui.data.Constants.POSITION


@AndroidEntryPoint
class Gallery1 : Fragment() {
    private var _binding: GalleryBinding? = null
    private val binding get() = _binding!!
    private var bundle = Bundle()
    lateinit var photosRecycleView: RecyclerView

    @Inject
    lateinit var photoAdapter:
            PhotoBigAdapterRecyclerView
    private val viewModel: HomeViewModel by viewModels()

    private var type = ""
    private var filmId: Int = 0
    private val keyList = listOf<String>(
        "STILL", "SHOOTING",
        "POSTER", "SCREENSHOT", "FAN_ART", "PROMO", "CONCEPT",
        "WALLPAPER", "COVER"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GalleryBinding.inflate(inflater, container, false)
        photoAdapter = PhotoBigAdapterRecyclerView()
        arguments?.let {
            filmId = it.getInt(FILM_ID)

        }
        binding.layoutWithHeader.setHeader(resources.getString(R.string.gallery))
        binding.layoutWithHeader.binding.buttonBack.setOnClickListener {
            bundle = Bundle().apply {
                putInt(FILM_ID, filmId)
            }
            findNavController().navigate(R.id.action_global_cardOfFilm, bundle)
        }

        photosRecycleView = binding.photoVerticalRecyclerView
        photosRecycleView.adapter = photoAdapter
        val spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {

            override fun getSpanSize(position: Int): Int {
                return if ((position + 1) % 3 != 0) 1
                else 2
            }
        }
        val greedLayoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        photosRecycleView.layoutManager = greedLayoutManager
        greedLayoutManager.spanSizeLookup = spanSizeLookup

        val listOfButtons = listOf<Button>(
            binding.buttonBlueFirst,
            binding.buttonBlueSecond,
            binding.buttonBlueThird,
            binding.buttonBlueForth,
            binding.buttonBlueFifth
        )
        listOfButtons.onEach { it ->
            it.setBackgroundResource(R.drawable.button_for_job_grey)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            var buttonCounter = 1
            var keys = 0
            while (buttonCounter in 1..5 && keys < 9) {
                val result = checkPosters(keyList[keys], buttonCounter) //ect
                if (result > buttonCounter) {
                    val button = listOfButtons[buttonCounter - 1]
                    button.visibility = View.VISIBLE
                    button.text = transformKeyToHeader(keyList[keys])
                    if (buttonCounter == 1) {
                        button.setBackgroundResource(R.drawable.button_for_job_blue)
                        val key = transformHeaderToKey(button.text as String)
                        lifecycleScope.launch {
                            try {
                                delay(1000)
                                viewModel.loadPoster(filmId, key, 1)
                            } catch (e: Exception) {
                                findNavController().navigate(R.id.action_global_bottomFragmentMistake)
                                findNavController().navigate(
                                    R.id.action_global_navigation_home,
                                    null
                                )
                            }
                            type = key
                            viewModel.poster.onEach {
                                photoAdapter.setData(it, ::onPhotoClick)
                            }.launchIn(viewLifecycleOwner.lifecycleScope)
                        }
                    }
                    button.setOnClickListener {
                        button
                        val key = transformHeaderToKey(button.text as String)
                        listOfButtons.onEach { it ->
                            it.setBackgroundResource(R.drawable.button_for_job_grey)
                        }
                        button.setBackgroundResource(R.drawable.button_for_job_blue)
                        lifecycleScope.launch {
                            try {
                                viewModel.loadPoster(filmId, key, 1)
                            } catch (e: Exception) {
                                findNavController().navigate(R.id.action_global_bottomFragmentMistake)
                                findNavController().navigate(
                                    R.id.action_global_navigation_home,
                                    null
                                )
                            }
                        }
                        type = key
                        viewModel.poster.onEach {
                            photoAdapter.setData(it, ::onPhotoClick)
                        }.launchIn(viewLifecycleOwner.lifecycleScope)

                    }
                    keys++
                    buttonCounter++
                } else {
                    keys++
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onPhotoClick(item: Photo, position: Int) {

        bundle = Bundle().apply {
            putString(URL_IMAGE, item.imageUrl)
            putString(TYPE, type)
            putInt(POSITION, position)
            putInt(FILM_ID, filmId)
        }
        findNavController().navigate(R.id.action_global_bigPhotoWithScrolling, bundle)
    }

    private suspend fun checkPosters(key: String, counter: Int): Int {
        var result = counter
        try {
            viewModel.loadPoster(filmId, key, 1)
        } catch (e: Exception) {
            findNavController().navigate(R.id.action_global_bottomFragmentMistake)
            findNavController().navigate(R.id.action_global_navigation_home, null)
        }
        delay(80)
        if (viewModel.poster.value.isNotEmpty()) {
            result++
        }
        return result
    }

    private fun transformKeyToHeader(key: String): String = when (key) {
        "STILL" -> "кадры"
        "SHOOTING" -> "изображения со съемок"
        "POSTER" -> "постеры"
        "FAN_ART" -> "фан-арты"
        "PROMO" -> "промо"
        "CONCEPT" -> "концепт-арты"
        "WALLPAPER" -> "обои"
        "COVER" -> "обложки"
        else -> "скриншоты"
    }

    private fun transformHeaderToKey(key: String): String = when (key) {
        "кадры" -> "STILL"
        "изображения со съемок" -> "SHOOTING"
        "постеры" -> "POSTER"
        "фан-арты" -> "FAN_ART"
        "промо" -> "PROMO"
        "концепт-арты" -> "CONCEPT"
        "обои" -> "WALLPAPER"
        "обложки" -> "COVER"
        else -> "SCREENSHOT"
    }

}