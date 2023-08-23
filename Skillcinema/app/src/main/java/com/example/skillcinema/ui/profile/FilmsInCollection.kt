package com.example.skillcinema.ui.profile

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
import com.example.skillcinema.ui.data.Constants.FOLDER_ID
import com.example.skillcinema.ui.data.Constants.HEADER
import com.example.skillcinema.ui.data.Constants.TYPE
import com.example.skillcinema.ui.data.FilmDB
import com.example.skillcinema.ui.data.RoomModule
import com.example.skillcinema.ui.data.RoomRepository
import com.example.skillcinema.ui.support.DbMovieSecondViewAdapterRecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class FilmsInCollection : Fragment() {
    private val viewModel: ProfileViewModel by viewModels()

    @Inject
    lateinit var roomRepository: RoomRepository
    private var _binding: ListOfFilmsBinding? = null
    private val binding get() = _binding!!
    private var bundle = Bundle()
    private lateinit var moviesRecyclerView: RecyclerView

    @Inject
    lateinit var moviesAdapter: DbMovieSecondViewAdapterRecyclerView
    private var header: String? = null
    private var folderId: Long? = 0
    private var type: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ListOfFilmsBinding.inflate(inflater, container, false)
        roomRepository =
            RoomRepository(RoomModule().providesDao(RoomModule().providesRoomDatabase(requireContext())))
        moviesAdapter = DbMovieSecondViewAdapterRecyclerView()

        arguments?.let {
            header = it.getString(HEADER)
            folderId = it.getLong(FOLDER_ID)
            type = it.getString(TYPE)
        }
        when (type) {
            null -> binding.layoutWithHeader.setHeader("Коллекция $header")
            else -> binding.layoutWithHeader.setHeader(header!!)
        }
        binding.layoutWithHeader.binding.buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_global_navigation_profile)
        }
        moviesRecyclerView = binding.listOfFilmsGridRecyclerView
        moviesRecyclerView.adapter = moviesAdapter
        moviesRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        var listOfFilms = listOf<FilmDB>()
        val scope = CoroutineScope(Dispatchers.Default)
        when (type) {
            "interesting" -> {
                scope.launch {
                    roomRepository.allFilms()
                        .collect {
                            listOfFilms = it
                        }
                }
            }
            "watched" -> {
                scope.launch {
                    roomRepository.listOfWatchedFilms()
                        .collect {
                            listOfFilms = it
                        }
                }
            }
            else -> {
                if (folderId != null || folderId!! > 0) {
                    scope.launch {
                        listOfFilms = roomRepository.getFilmsInFolder(folderId!!)
                        println("listOfFilms in Folder $header = $listOfFilms")
                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            delay(500)
            moviesAdapter.setData(listOfFilms, ::onItemClick)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onItemClick(item: FilmDB) {
        bundle = Bundle().apply {
            putInt(FILM_ID, item.idFilm)
        }
        findNavController().navigate(R.id.action_global_cardOfFilm, bundle)
    }
}