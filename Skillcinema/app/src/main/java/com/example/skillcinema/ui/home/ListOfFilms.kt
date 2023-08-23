package com.example.skillcinema.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.R
import com.example.skillcinema.databinding.ListOfFilmsBinding
import com.example.skillcinema.ui.models.Movie
import com.example.skillcinema.ui.support.AdapterRecyclerView
import com.example.skillcinema.ui.support.MoviePagedListAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import com.example.skillcinema.ui.data.Constants.COUNTRY
import com.example.skillcinema.ui.data.Constants.HEADER
import com.example.skillcinema.ui.data.Constants.REQUEST_ID
import com.example.skillcinema.ui.data.Constants.FILM_ID
import com.example.skillcinema.ui.data.Constants.GENRE
import com.example.skillcinema.ui.data.Constants.PAGED
import com.example.skillcinema.ui.data.Constants.POPULAR
import com.example.skillcinema.ui.data.Constants.PREMIERES
import com.example.skillcinema.ui.data.Constants.RANDOM1
import com.example.skillcinema.ui.data.Constants.RANDOM2
import com.example.skillcinema.ui.data.Constants.TOP
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListOfFilms : Fragment() {
    private var _binding: ListOfFilmsBinding? = null
    private val binding get() = _binding!!
    private var bundle = Bundle()
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var moviesRecyclerView: RecyclerView
    @Inject
    lateinit var pagedAdapter: MoviePagedListAdapter
    @Inject
    lateinit var moviesAdapter: AdapterRecyclerView
    private var header: String? = null
    private var idOfRequest: String? = null
    private var country: Int = 0
    private var genre: Int = 0
    private var filmId: Int = 0
    private var pagedRequest: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ListOfFilmsBinding.inflate(inflater, container, false)
        pagedAdapter = MoviePagedListAdapter()
        moviesAdapter = AdapterRecyclerView()
        arguments?.let {
            header = it.getString(HEADER)
            idOfRequest = it.getString(REQUEST_ID)
            country = it.getInt(COUNTRY)
            genre = it.getInt(GENRE)
            filmId = it.getInt(FILM_ID)
            pagedRequest = it.getBoolean(PAGED)
        }
        binding.layoutWithHeader.setHeader(header!!)
        binding.layoutWithHeader.binding.buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_global_navigation_home, null)
        }
        moviesRecyclerView = binding.listOfFilmsGridRecyclerView
        moviesRecyclerView.layoutManager = GridLayoutManager(context, 2)
        if (pagedRequest) {
            val pagedRequest: Flow<PagingData<Movie>> = when (idOfRequest) {
                POPULAR -> viewModel.pagedPopularMovies
                TOP -> viewModel.pagedTopMovies
                RANDOM1 -> viewModel.pagedRandom1(country, genre)
                RANDOM2 -> viewModel.pagedRandom2(country, genre)
                else -> viewModel.pagedSerialMovies
            }
            moviesRecyclerView.adapter = pagedAdapter
            pagedAdapter.loadParameters(::onItemClick)
            pagedRequest.onEach {
                pagedAdapter.submitData(it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        } else {
            val request: StateFlow<List<Movie>> = when (idOfRequest) {
                PREMIERES -> {
                    viewModel.loadPremieres()
                    viewModel.premieres
                }
                else -> {
                    viewModel.loadSimilarFilms(filmId)
                    viewModel.similar
                }
            }
            moviesRecyclerView.adapter = moviesAdapter
            request.onEach {
                moviesAdapter.setData(it, ::onItemClick)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
}