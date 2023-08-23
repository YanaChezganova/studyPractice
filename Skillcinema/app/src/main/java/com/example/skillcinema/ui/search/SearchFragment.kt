package com.example.skillcinema.ui.search

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentSearchBinding
import com.example.skillcinema.ui.data.Constants.COUNTRY
import com.example.skillcinema.ui.data.Constants.FILM_ID
import com.example.skillcinema.ui.data.Constants.GENRE
import com.example.skillcinema.ui.data.Constants.ORDER
import com.example.skillcinema.ui.data.Constants.RATING_FROM
import com.example.skillcinema.ui.data.Constants.RATING_TO
import com.example.skillcinema.ui.data.Constants.SORT
import com.example.skillcinema.ui.data.Constants.WATCHED
import com.example.skillcinema.ui.data.Constants.YEAR_SINCE
import com.example.skillcinema.ui.data.Constants.YEAR_UNTIL
import com.example.skillcinema.ui.data.FilmDB
import com.example.skillcinema.ui.data.RoomModule
import com.example.skillcinema.ui.data.RoomRepository
import com.example.skillcinema.ui.models.Movie
import com.example.skillcinema.ui.models.State
import com.example.skillcinema.ui.support.DbMovieSecondViewAdapterRecyclerView
import com.example.skillcinema.ui.support.PagedAdapterWhiteRatingWithWatchedFilms
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private var bundle = Bundle()
    private lateinit var moviesRecyclerView: RecyclerView

    @Inject
    lateinit var pagedAdapter:
            PagedAdapterWhiteRatingWithWatchedFilms

    val viewModel: SearchViewModel by viewModels()

    @Inject
    lateinit var roomRepository: RoomRepository
    private var country: String? = null
    private var genre: String? = null
    private var yearSince: String? = null
    private var yearUntil: String? = null
    private var ratingFrom: String? = null
    private var ratingTo: String? = null
    private var watched: Boolean? = null
    private var type: String = "ALL"
    private var order: String = "RATING"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.searchView.queryHint = "Найду фильм..."
        pagedAdapter = PagedAdapterWhiteRatingWithWatchedFilms()
        roomRepository =
            RoomRepository(RoomModule().providesDao(RoomModule().providesRoomDatabase(requireContext())))

        val scope = CoroutineScope(Dispatchers.Default)
        var listOfWatchedFilms = listOf<FilmDB>()
        scope.launch {
            roomRepository.listOfWatchedFilms()
                .collect {
                    listOfWatchedFilms = it
                }
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(request: String?): Boolean {
                viewModel.loadSearchResults(request!!, 1)
                lifecycleScope.launchWhenCreated {

                    viewModel.stateOfView.collect { state ->
                        when (state) {
                            State.Loading -> {
                                binding.progressBar.isVisible = true
                            }
                            State.Done -> {
                                binding.progressBar.isVisible = false
                                val listForAdapter =
                                    makeListOfWatchedFilmsForRecyclerViewAdapter(listOfWatchedFilms)
                                if (viewModel.searchResult.value.isNotEmpty()) {
                                    binding.textNotFound.visibility = View.GONE
                                    moviesRecyclerView = binding.verticalFilmsRecyclerView
                                    moviesRecyclerView.adapter = pagedAdapter
                                    viewModel.pagedSearchResult(keyword = request!!).onEach {
                                        pagedAdapter.loadParameters(
                                            listForAdapter,
                                            null,
                                            ::onItemClick
                                        )
                                        pagedAdapter.submitData(it)
                                    }.launchIn(viewLifecycleOwner.lifecycleScope)
                                } else {
                                    viewModel.pagedSearchResult(keyword = request!!).onEach {
                                        pagedAdapter.loadParameters(
                                            listForAdapter,
                                            null,
                                            ::onItemClick
                                        )
                                        pagedAdapter.submitData(it)
                                    }.launchIn(viewLifecycleOwner.lifecycleScope)
                                    if (request.isEmpty()) {
                                        binding.textNotFound.visibility = View.GONE
                                    } else {
                                        binding.textNotFound.visibility = View.VISIBLE
                                    }
                                }
                            }
                            else -> {
                                findNavController().navigate(R.id.action_global_bottomFragmentMistake)
                            }
                        }
                    }
                }
                return false
            }
        })
        binding.verticalFilmsRecyclerView.addRecyclerListener {
            KeyboardUtils.hideKeyboard(requireActivity())
        }
        binding.separator.setOnClickListener {
            binding.searchView.setQuery("", false)
            binding.textNotFound.visibility = View.GONE
        }
        binding.searchView.setOnCloseListener {
            return@setOnCloseListener false
        }
        binding.buttonFilter.setOnClickListener {
            findNavController().navigate(R.id.action_global_searchSettings)
        }
        arguments?.let {
            country = it.getString(COUNTRY)
            genre = it.getString(GENRE)
            yearSince = it.getString(YEAR_SINCE)
            yearUntil = it.getString(YEAR_UNTIL)
            ratingFrom = it.getString(RATING_FROM)
            ratingTo = it.getString(RATING_TO)
            watched = it.getBoolean(WATCHED)
            type = it.getString(SORT)!!
            order = it.getString(ORDER)!!
        }
        println("after arguments $country, $genre, $order, $watched")

        if (country == null && genre == null &&
            yearSince == null && yearUntil == null &&
            ratingFrom == null && ratingTo == null && watched == null
        ) {
            Log.d("Search Fragment", "No settings")
        } else {
            var countryInt = 1
            viewModel.listOfCountries.forEach {
                if (it.country == country)
                    countryInt = it.id
            }
            var genreInt = 1
            viewModel.listOfGenres.forEach {
                if (it.genre == genre)
                    genreInt = it.id
            }
            lifecycleScope.launch {
                viewModel.loadSearchResultSettings(
                    countryInt, genreInt, order,
                    type,
                    ratingFrom!!, ratingTo!!,
                    yearSince!!, yearUntil!!, 1
                )
                delay(1000)
                if (viewModel.searchBySettings.value.isEmpty()) {
                    binding.textNotFound.visibility = View.VISIBLE
                } else {
                    binding.textNotFound.visibility = View.GONE
                   // if (watched == true) {
                        println("Show watched true")
                        moviesRecyclerView = binding.verticalFilmsRecyclerView
                        moviesRecyclerView.adapter = pagedAdapter
                        val listForAdapter =
                            makeListOfWatchedFilmsForRecyclerViewAdapter(listOfWatchedFilms)
                        val request = viewModel.pagedSearchBySettingsResult(
                            countryInt, genreInt, order,
                            type,
                            ratingFrom!!, ratingTo!!,
                            yearSince!!, yearUntil!!
                        )
                        delay(500)
                        request.onEach {
                            pagedAdapter.loadParameters(listForAdapter, watched, ::onItemClick)
                            pagedAdapter.submitData(it)
                        }.launchIn(viewLifecycleOwner.lifecycleScope)
                    //}
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun makeListOfWatchedFilmsForRecyclerViewAdapter(
        listOfFilmsInDB: List<FilmDB>
    ): List<Int> {
        val listOfFilmInDBId = mutableListOf<Int>()
        listOfFilmsInDB.onEach { film ->
            listOfFilmInDBId.add(film.idFilm)
        }
        return listOfFilmInDBId
    }
    // need to put in bundle search settings !!! TO BACK KEY


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

    class KeyboardUtils {
        companion object {
            fun hideKeyboard(activity: Activity) {
                val imm: InputMethodManager =
                    activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                var view: View? = activity.currentFocus
                if (view == null) {
                    view = View(activity)
                }
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

}