package com.example.skillcinema.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.R
import com.example.skillcinema.databinding.InfoActorBinding
import com.example.skillcinema.ui.models.Movie
import com.example.skillcinema.ui.models.State
import com.example.skillcinema.ui.support.AdapterRecyclerView
import com.google.android.material.shape.CornerFamily
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import com.example.skillcinema.ui.data.Constants.NAME
import com.example.skillcinema.ui.data.Constants.PERSON_ID
import com.example.skillcinema.ui.data.Constants.FILM_ID
import com.example.skillcinema.ui.data.Constants.URL_IMAGE
import com.example.skillcinema.ui.data.Constants.PROFESSION
import com.example.skillcinema.ui.data.Constants.HEADER
import javax.inject.Inject

@AndroidEntryPoint
class InfoActor : Fragment() {
    private var _binding: InfoActorBinding? = null
    private val binding get() = _binding!!
    var bundle = Bundle()
    private lateinit var moviesRecyclerView: RecyclerView

    @Inject
    lateinit var moviesAdapter: AdapterRecyclerView
    private val viewModel: HomeViewModel by viewModels()
    private var name: String? = null
    private var idOfPerson: Int? = null
    private var idOfFilm: Int? = null
    private var url: String? = null
    private var profession: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = InfoActorBinding.inflate(inflater, container, false)
        moviesAdapter = AdapterRecyclerView()

        binding.progressBar.isVisible = true
        arguments?.let {
            name = it.getString(NAME)
            idOfPerson = it.getInt(PERSON_ID)
            url = it.getString(URL_IMAGE)
            idOfFilm = it.getInt(FILM_ID)
            profession = it.getString(PROFESSION)
        }
        binding.buttonBack.setOnClickListener {
            bundle = Bundle().apply {
                putInt(FILM_ID, idOfFilm!!)
            }
            findNavController().navigate(R.id.action_global_cardOfFilm, bundle)
        }

        lifecycleScope.launchWhenCreated {
            viewModel.loadPersonInformation(idOfPerson!!)
            delay(500)

            viewModel.stateOfView.collect { state ->
                when (state) {
                    State.Loading -> {
                        binding.progressBar.isVisible = true
                    }
                    State.Done -> {
                        binding.progressBar.isVisible = false
                        binding.personNameRu.text =
                            if (name == null) viewModel.personInfo.nameRu!! else name
                        if (viewModel.personInfo.nameEn != null) {
                            binding.personNameEn.text = viewModel.personInfo.nameEn
                            binding.personNameEn.visibility = View.VISIBLE
                        }
                        binding.personImage.shapeAppearanceModel
                            .toBuilder()
                            .setTopRightCorner(CornerFamily.ROUNDED, 16.0F)
                            .setTopLeftCorner(CornerFamily.ROUNDED, 16.0F)
                            .setBottomLeftCorner(CornerFamily.ROUNDED, 16.0F)
                            .setBottomRightCorner(CornerFamily.ROUNDED, 16.0F)
                            .build()
                        if (url == null) url = viewModel.personInfo.posterUrl
                        Glide.with(binding.personImage.context)
                            .load(url)
                            .into(binding.personImage)
                        binding.personRole.text = viewModel.personInfo.profession
                        moviesRecyclerView =
                            binding.bestFilmsCustomView.binding.viewGroupHorizontalRecyclerView
                        moviesRecyclerView.adapter = moviesAdapter
                        val listOfFilmsToWork = viewModel.personInfo.films.distinct()

                        val sizeFilmography = listOfFilmsToWork.size
                        val sortedList = mutableListOf<Movie>()
                        var maxRating = findMaxRating(listOfFilmsToWork)
                        val minRating = maxRating - 1.5
                        while (maxRating >= minRating) {
                            listOfFilmsToWork.onEach {
                                if (it.rating != null) {
                                    if (it.rating.toDouble() == maxRating)
                                        sortedList.add(it)
                                }
                            }
                            maxRating = ((maxRating - 0.1) * 100.0).roundToInt() / 100.0
                        }
                        moviesAdapter.setData(sortedList, ::onItemClick)
                        binding.textCountOfAllFilms.text = resources.getQuantityString(
                            R.plurals.count_of_films_by_actor,
                            sizeFilmography,
                            sizeFilmography
                        )
                    }
                    else -> {
                        binding.progressBar.isVisible = true
                        findNavController().navigate(R.id.action_global_bottomFragmentMistake)

                    }
                }
            }
        }

        ViewCompat.setTransitionName(binding.personImage, "person_photo")
        binding.personImage.setOnClickListener {
            bundle = Bundle().apply {
                putString(URL_IMAGE, url)
            }
            val extras = FragmentNavigatorExtras(binding.personImage to "hero_image")
            view?.findNavController()?.navigate(
                R.id.action_global_galleryBigPhoto,
                bundle,
                null,
                extras
            )
        }

        binding.bestFilmsCustomView.setHeader(resources.getString(R.string.best))
        binding.bestFilmsCustomView.setBlueArrowVisible()

        binding.bestFilmsCustomView.binding.buttonAllElements.setOnClickListener {
            bundle = Bundle().apply {
                putInt(PERSON_ID, idOfPerson!!)
                putString(HEADER, resources.getString(R.string.films))
                putInt(FILM_ID, idOfFilm!!)
            }
            findNavController().navigate(R.id.action_global_listOfPersonsFilms, bundle)
        }
        binding.buttonCountOfFilms.setOnClickListener {
            bundle = Bundle().apply {
                putInt(PERSON_ID, idOfPerson!!)
                putString(HEADER, resources.getString(R.string.filmography))
                putInt(FILM_ID, idOfFilm!!)
            }
            findNavController().navigate(R.id.action_global_filmography, bundle)
        }

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        moviesRecyclerView.adapter = null
        super.onDestroyView()

    }

    fun onItemClick(item: Movie) {
        if (item.kinopoiskId > item.filmId)
            bundle = Bundle().apply {
                putInt(FILM_ID, item.kinopoiskId)
            }
        else bundle = Bundle().apply {
            putInt(FILM_ID, item.filmId)
        }
        findNavController().navigate(R.id.action_global_cardOfFilm, bundle)
    }

    fun findMaxRating(list: List<Movie>): Double {
        var previous = 1.0
        var max = 1.0
        for (i in list.indices) {
            previous = max
            if (list[i].rating != null) {
                max = list[i].rating?.toDouble()!!.coerceAtLeast(previous)
            }
        }
        return max
    }
}