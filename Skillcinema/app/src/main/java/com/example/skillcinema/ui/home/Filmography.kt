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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.Application
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FilmographyBinding
import com.example.skillcinema.ui.data.CinemaBaseDao
import com.example.skillcinema.ui.data.Repository
import com.example.skillcinema.ui.models.Movie
import com.example.skillcinema.ui.support.MovieWhiteRatingAdapterRecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject
import com.example.skillcinema.ui.data.Constants.FILM_ID
import com.example.skillcinema.ui.data.Constants.HEADER
import com.example.skillcinema.ui.data.Constants.PERSON_ID


@AndroidEntryPoint
class Filmography : Fragment() {
    private var _binding: FilmographyBinding? = null
    private val binding get() = _binding!!
    private var bundle = Bundle()
    lateinit var moviesRecyclerView: RecyclerView
    private lateinit var chipGroup: ChipGroup

    @Inject
    lateinit var moviesAdapter: MovieWhiteRatingAdapterRecyclerView
    private val viewModel: HomeViewModel by viewModels()

    private var header: String? = null
    private var personId: Int = 0
    private var filmId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FilmographyBinding.inflate(inflater, container, false)
        moviesAdapter = MovieWhiteRatingAdapterRecyclerView()
        arguments?.let {
            header = it.getString(HEADER)
            personId = it.getInt(PERSON_ID)
            filmId = it.getInt(FILM_ID)
        }
        binding.layoutWithHeader.setHeader(header!!)
        binding.layoutWithHeader.binding.buttonBack.setOnClickListener {
            bundle = Bundle().apply {
                putInt(PERSON_ID, personId)
                putInt(FILM_ID, filmId)

            }
            findNavController().navigate(R.id.action_global_infoActor, bundle)
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.loadPersonInformation(personId)
            delay(800)
            binding.textActorName.text =
                if (viewModel.personInfo.nameRu == null)
                    viewModel.personInfo.nameEn
                else
                    viewModel.personInfo.nameRu
            val listOfMovie = viewModel.personInfo.films
            val listOfProfessions = countProfessionButtons(listOfMovie)
            moviesRecyclerView = binding.filmsVerticalRecyclerView
            moviesRecyclerView.adapter = moviesAdapter
            chipGroup = binding.chipGroup
            listOfProfessions.onEach {
                addChip(listOfMovie, it)
            }
            if (listOfProfessions.isNotEmpty()) {
                val nameJob = listOfProfessions.first()
                sortListOfFilmsByProfession(listOfMovie, nameJob)
                val sortedList = sortListOfFilmsByProfession(listOfMovie, nameJob)
                moviesAdapter.setData(sortedList, ::onItemClick)
            } else {
                findNavController().navigate(R.id.action_global_bottomFragmentMistake)
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onItemClick(item: Movie) {
        bundle = if (item.kinopoiskId > item.filmId)
            Bundle().apply {
                putInt(FILM_ID, item.kinopoiskId)
            }
        else Bundle().apply {
            putInt(FILM_ID, item.filmId)
        }
        findNavController().navigate(R.id.action_global_cardOfFilm, bundle)
    }

    private fun countProfessionButtons(listOfMovie: List<Movie>): List<String> {
        val listOfPersonsProfessions: MutableList<String> = mutableListOf()
        kotlin.runCatching {
            listOfMovie.onEach {
                listOfPersonsProfessions.add(it.professionKey!!)
            }
        }.onFailure { _: Throwable ->
            findNavController().navigate(R.id.action_global_bottomFragmentMistake)

        }
        return listOfPersonsProfessions.distinct()
    }

    private fun sortListOfFilmsByProfession(list: List<Movie>, professionKey: String): List<Movie> {
        val listOfRequiredProfessions: MutableList<Movie> = mutableListOf()
        list.onEach {
            if (it.professionKey == professionKey)
                listOfRequiredProfessions.add(it)
        }
        return listOfRequiredProfessions
    }

    private fun translateProfession1(professionKey: String): String {
        var keyWord = professionKey
        if (viewModel.personInfo.sex == "MALE" && keyWord == "ACTOR") {
            keyWord = "ACTOR1"
        }
        return when (keyWord) {
            "WRITER" -> "Сценарист"
            "OPERATOR" -> "Оператор"
            "EDITOR" -> "Монтажёр"
            "COMPOSER" -> "Композитор"
            "PRODUCER_USSR" -> "Режиссер"
            "TRANSLATOR" -> "Переводчик"
            "DIRECTOR" -> "Директор"
            "DESIGN" -> "Оформление"
            "PRODUCER" -> "Режиссер"
            "ACTOR" -> "Актриса"
            "ACTOR1" -> "Актёр"
            "VOICE_DIRECTOR" -> "Режиссер звукозаписи"
            "VOICE_FEMALE" -> "Актриса дубляжа"
            "VOICE_MALE" -> "Актер дубляжа"
            "HERSELF" -> "Актриса: играет саму себя"
            "HIMSELF" -> "Актёр: играет сам себя"
            "HRONO_TITR_FEMALE" -> "Актриса: Хроника, В титрах не указана"
            "HRONO_TITR_MALE" -> "Актёр: Хроника, В титрах не указан"
            else -> "Участие в создании"
        }
    }

    private fun addChip(listOfMovie: List<Movie>, nameJob: String) {
        val chip = Chip(this.requireContext())
        chipGroup.addView(chip)
        var listForButton = listOf<Movie>()
        listForButton = sortListOfFilmsByProfession(listOfMovie, nameJob)
        println("fun sorter $listForButton")
        val size = listForButton.size
        chip.text = resources.getString(R.string.job_and_count, translateProfession1(nameJob), size)
        chip.setOnClickListener {
            moviesAdapter.setData(listForButton, ::onItemClick)
        }
    }
}