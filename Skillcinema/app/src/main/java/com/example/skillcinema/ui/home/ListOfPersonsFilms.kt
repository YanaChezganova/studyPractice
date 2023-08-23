package com.example.skillcinema.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.R
import com.example.skillcinema.databinding.ListOfFilmsBinding
import com.example.skillcinema.ui.models.Movie
import com.example.skillcinema.ui.support.AdapterRecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject
import com.example.skillcinema.ui.data.Constants.HEADER
import com.example.skillcinema.ui.data.Constants.PERSON_ID
import com.example.skillcinema.ui.data.Constants.FILM_ID

@AndroidEntryPoint
class ListOfPersonsFilms: Fragment() {
    private var _binding: ListOfFilmsBinding? = null
    private val binding get() = _binding!!
    private var bundle = Bundle()
    private lateinit var moviesRecyclerView: RecyclerView
    @Inject
    lateinit var moviesAdapter: AdapterRecyclerView //{ movie -> onItemClick(movie) }
    private val viewModel: HomeViewModel by viewModels()
    private var header: String? = null
    private var personId: Int = 0
    private var filmId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ListOfFilmsBinding.inflate(inflater, container, false)
        moviesAdapter = AdapterRecyclerView()

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
        lifecycleScope.launchWhenCreated  {
            viewModel.loadPersonInformation(personId)
            delay(600)
        moviesRecyclerView = binding.listOfFilmsGridRecyclerView
        moviesRecyclerView.layoutManager = GridLayoutManager(context,2)
        moviesRecyclerView.adapter = moviesAdapter
        moviesAdapter.setData(viewModel.personInfo.films, ::onItemClick)
        }
        return binding.root
        }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun onItemClick(item: Movie){
        bundle = if (item.kinopoiskId > item.filmId)
            Bundle().apply {
                putInt(FILM_ID, item.kinopoiskId)
            }
        else Bundle().apply {
            putInt(FILM_ID, item.filmId)
        }
        findNavController().navigate(R.id.action_global_cardOfFilm, bundle)
    }
}