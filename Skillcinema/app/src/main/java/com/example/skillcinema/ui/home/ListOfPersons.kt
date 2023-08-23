package com.example.skillcinema.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.R
import com.example.skillcinema.databinding.ListOfFilmsBinding
import com.example.skillcinema.ui.data.Constants.FILM_ID
import com.example.skillcinema.ui.data.Constants.HEADER
import com.example.skillcinema.ui.data.Constants.PERSON_ID
import com.example.skillcinema.ui.data.Constants.TYPE
import com.example.skillcinema.ui.models.Person
import com.example.skillcinema.ui.support.PersonAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@AndroidEntryPoint
class ListOfPersons : Fragment() {
    private var _binding: ListOfFilmsBinding? = null
    private val binding get() = _binding!!
    private var bundle = Bundle()
    private lateinit var personRecyclerView: RecyclerView

    @Inject
    lateinit var personAdapter: PersonAdapter
    private val viewModel: HomeViewModel by viewModels()
    private var header: String? = null
    private var type: String? = null
    private var filmId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ListOfFilmsBinding.inflate(inflater, container, false)
        personAdapter = PersonAdapter()

        arguments?.let {
            header = it.getString(HEADER)
            filmId = it.getInt(FILM_ID)
            type = it.getString(TYPE)
        }

        binding.layoutWithHeader.setHeader(header!!)
        binding.layoutWithHeader.binding.buttonBack.setOnClickListener {
            bundle = Bundle().apply {
                putInt(FILM_ID, filmId)
            }
            findNavController().navigate(R.id.action_global_cardOfFilm, bundle)
        }
        personRecyclerView = binding.listOfFilmsGridRecyclerView
        personRecyclerView.adapter = personAdapter
        personRecyclerView.layoutManager =
            LinearLayoutManager(context)
        lifecycleScope.launchWhenCreated {
            viewModel.loadMovieStaff(filmId)
            delay(700)
            val request: StateFlow<List<Person>> = when (type) {
                "ACTORS" -> {
                    viewModel.staffActor
                }
                else -> {
                    viewModel.staffOther
                }
            }
            personRecyclerView.adapter = personAdapter

            request.onEach {
                personAdapter.setData(it, ::onPersonClick)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        }
        return binding.root
    }

    override fun onDestroyView() {

        personRecyclerView.adapter = null
        //personRecyclerView = null
        _binding = null
        super.onDestroyView()
    }

    private fun onPersonClick(item: Person) {
        bundle = if (item.personId > item.staffId)
            Bundle().apply {
                putInt(PERSON_ID, item.personId)
                putInt(FILM_ID, filmId)
            }
        else Bundle().apply {
            putInt(PERSON_ID, item.staffId)
            putInt(FILM_ID, filmId)

        }
        findNavController().navigate(R.id.action_global_infoActor, bundle)
    }
}