package com.example.skillcinema.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentHomeBinding
import com.example.skillcinema.ui.data.*
import com.example.skillcinema.ui.data.Constants.FILM_ID
import com.example.skillcinema.ui.data.Constants.POPULAR
import com.example.skillcinema.ui.data.Constants.PREMIERES
import com.example.skillcinema.ui.data.Constants.RANDOM1
import com.example.skillcinema.ui.data.Constants.RANDOM2
import com.example.skillcinema.ui.data.Constants.SERIAL
import com.example.skillcinema.ui.data.Constants.TOP
import com.example.skillcinema.ui.models.Movie
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.annotations.TestOnly
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private var bundle = Bundle()
    private val viewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var roomRepository: RoomRepository //(RoomModule().providesDao(RoomModule().providesRoomDatabase(requireContext())))

    private val binding get() = _binding!!
    private var parameterRandomList1 = listOf<Int>()
    private var parameterRandomList2 = listOf<Int>()
    private val date = System.currentTimeMillis()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        roomRepository =
            RoomRepository(RoomModule().providesDao(RoomModule().providesRoomDatabase(requireContext())))

        val scope = CoroutineScope(Dispatchers.Default)
        var listOfWatchedFilms = listOf<FilmDB>()
        var allFolders = listOf<FolderDB>()
        scope.launch {
            roomRepository.allFolders()
                .collect {
                    allFolders = it
                }
        }

        viewModel.loadPremieres()
        viewModel.loadPopular(page = 1)
        binding.progressBar.isVisible = true
        scope.launch {
            roomRepository.listOfWatchedFilms()
                .collect {
                    listOfWatchedFilms = it
                }
        }
        lifecycleScope.launchWhenCreated {
            delay(1500)
            //making folders "favorite" and "want to see" at first start of app
            // types of folders: 1 - favorite, 2 - want to see, 3 - person
            println("all folder from home fragment = $allFolders")
            if (allFolders.isEmpty()) {
                roomRepository.addFolderInBase(1, date, resources.getString(R.string.favorite))
                roomRepository.addFolderInBase(
                    2,
                    date + 15,
                    resources.getString(R.string.want_to_see)
                )
            }

            binding.firstView.setHeader(getString(R.string.premiere))
            binding.firstView.isVisible = true
            var list = emptyList<Movie>()
            viewModel.premieres.onEach {
                binding.progressBar.isVisible = false
                list = it.take(20)
                val listForAdapter = makeListOfWatchedFilmsForRecyclerViewAdapter(
                    list,
                    listOfWatchedFilms
                )

                binding.firstView.makeListWithFilms(
                    list,
                    listForAdapter,
                    getString(R.string.premiere),
                    PREMIERES,
                    0,
                    0,
                    false
                )
            }.launchIn(viewLifecycleOwner.lifecycleScope)
            binding.firstView.binding.buttonAllElements.setOnClickListener {
                binding.firstView.onAllClick(
                    getString(R.string.premiere),
                    PREMIERES,
                    0,
                    0,
                    false
                )
            }

            binding.secondView.setHeader(getString(R.string.popular))
            binding.secondView.isVisible = true
            viewModel.popular.onEach {
                binding.progressBar.isVisible = false
                val listForAdapter =
                    makeListOfWatchedFilmsForRecyclerViewAdapter(it, listOfWatchedFilms)
                binding.secondView.makeListWithFilms(
                    it,
                    listForAdapter,
                    getString(R.string.popular),
                    POPULAR,
                    0,
                    0,
                    true
                )
            }.launchIn(viewLifecycleOwner.lifecycleScope)
            binding.secondView.binding.buttonAllElements.setOnClickListener {
                binding.secondView.onAllClick(
                    getString(R.string.popular),
                    POPULAR,
                    0,
                    0,
                    true
                )
            }
            binding.thirdView.isVisible = true
            parameterRandomList1 = if (savedInstanceState?.getIntArray(RANDOM1) == null) {
                getRandomParameters()
            } else {
                savedInstanceState.getIntArray(RANDOM1)?.toList() ?: listOf(3, 6)
            }
            viewModel.getRandomListOfFilms(
                parameterRandomList1[0],
                parameterRandomList1[1],
                viewModel._randomFirstList,
                1
            )
            if (viewModel.randomFirstList == emptyList<Movie>()) {
                parameterRandomList1 = listOf(3, 6)
            }
            viewModel.getRandomListOfFilms(
                parameterRandomList1[0],
                parameterRandomList1[1],
                viewModel._randomFirstList,
                1
            )
            binding.thirdView.setHeader(getHeader(parameterRandomList1[0], parameterRandomList1[1]))
            binding.thirdView.binding.textAllElements.setOnClickListener {
                binding.thirdView.onAllClick(
                    getHeader(parameterRandomList1[0], parameterRandomList1[1]),
                    RANDOM1, parameterRandomList1[0], parameterRandomList1[1], true
                )
            }
            viewModel.randomFirstList.onEach {
                val listForAdapter =
                    makeListOfWatchedFilmsForRecyclerViewAdapter(it, listOfWatchedFilms)
                binding.thirdView.makeListWithFilms(
                    it, listForAdapter, getHeader(parameterRandomList1[0], parameterRandomList1[1]),
                    RANDOM1, parameterRandomList1[0], parameterRandomList1[1], true
                )
            }.launchIn(viewLifecycleOwner.lifecycleScope)

            binding.forthView.setHeader(getString(R.string.top_250))
            binding.forthView.isVisible = true
            viewModel.loadTopList(1)
            viewModel.topList.onEach {
                val listForAdapter =
                    makeListOfWatchedFilmsForRecyclerViewAdapter(it, listOfWatchedFilms)

                binding.forthView.makeListWithFilms(
                    it,
                    listForAdapter,
                    getString(R.string.top_250),
                    TOP,
                    0,
                    0,
                    true
                )
            }.launchIn(viewLifecycleOwner.lifecycleScope)
            binding.forthView.binding.buttonAllElements.setOnClickListener {
                binding.forthView.onAllClick(getString(R.string.top_250), "top", 0, 0, true)
            }

            parameterRandomList2 = if (savedInstanceState?.getIntArray(RANDOM2) == null) {
                getRandomParameters()
            } else {
                savedInstanceState.getIntArray(RANDOM2)?.toList() ?: listOf(34, 5)
            }
            viewModel.getRandomListOfFilms(
                parameterRandomList2[0],
                parameterRandomList2[1],
                viewModel._randomSecondList,
                1
            )
            if (viewModel.randomSecondList == emptyList<Movie>()) {
                parameterRandomList2 = listOf(34, 5)
            }
            viewModel.getRandomListOfFilms(
                parameterRandomList2[0],
                parameterRandomList2[1],
                viewModel._randomSecondList,
                1
            )
            binding.fifthView.isVisible = true
            binding.fifthView.setHeader(getHeader(parameterRandomList2[0], parameterRandomList2[1]))
            binding.fifthView.binding.textAllElements.setOnClickListener {
                binding.fifthView.onAllClick(
                    getHeader(parameterRandomList2[0], parameterRandomList2[1]),
                    RANDOM2, parameterRandomList2[0], parameterRandomList2[1], true
                )
            }
            viewModel.randomSecondList.onEach {
                val listForAdapter =
                    makeListOfWatchedFilmsForRecyclerViewAdapter(it, listOfWatchedFilms)
                binding.fifthView.makeListWithFilms(
                    it, listForAdapter, getHeader(parameterRandomList2[0], parameterRandomList2[1]),
                    RANDOM2, parameterRandomList2[0], parameterRandomList2[1], true
                )
            }.launchIn(viewLifecycleOwner.lifecycleScope)

            binding.sixthView.setHeader(getString(R.string.serials))
            binding.sixthView.isVisible = true
            viewModel.loadSerials(1)
            viewModel.serials.onEach {
                val listForAdapter =
                    makeListOfWatchedFilmsForRecyclerViewAdapter(it, listOfWatchedFilms)

                binding.sixthView.makeListWithFilms(
                    it,
                    listForAdapter,
                    getString(R.string.serials),
                    SERIAL,
                    0,
                    0,
                    true
                )
            }.launchIn(viewLifecycleOwner.lifecycleScope)
            binding.sixthView.binding.buttonAllElements.setOnClickListener {
                binding.sixthView.onAllClick(getString(R.string.serials), SERIAL, 0, 0, true)
            }
        }
        return binding.root
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getRandomParameters(): List<Int> {
        val genreId = Random.nextInt(1, 24)
        val countryId = Random.nextInt(1, 25)
        return listOf(genreId, countryId)
    }

    private fun makeListOfWatchedFilmsForRecyclerViewAdapter(
        listOfFilms: List<Movie>,
        listOfFilmsInDB: List<FilmDB>
    ): List<Int> {
        val listOfMovieId = mutableListOf<Int>()
        listOfFilms.onEach { movie ->
            if (movie.kinopoiskId > movie.filmId)
                listOfMovieId.add(movie.kinopoiskId)
            else listOfMovieId.add(movie.filmId)
        }
        val listOfFilmInDBId = mutableListOf<Int>()
        listOfFilmsInDB.onEach { film ->
            listOfFilmInDBId.add(film.idFilm)
        }
        val listOfWatchedId = mutableListOf<Int>()
        listOfMovieId.onEach {
            if (listOfFilmInDBId.contains(it))
                listOfWatchedId.add(it)
        }
        return listOfWatchedId
    }
  fun getHeader(country: Int, genre: Int): String {
        var countryText = ""
        var genreText = ""
        viewModel.listOfCountries.forEach {
            if (it.id == country)
                countryText = it.country
                    .replaceFirstChar { text -> if (text.isLowerCase()) text.titlecase(Locale.getDefault()) else text.toString() }
        }
        viewModel.listOfGenres.forEach {
            if (it.id == genre)
                genreText = it.genre
                    .replaceFirstChar { text -> if (text.isLowerCase()) text.titlecase(Locale.getDefault()) else text.toString() }
        }
        return resources.getString(
            R.string.custom_selection,
            genreText,
            countryText
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putIntArray(RANDOM1, parameterRandomList1.toIntArray())
        outState.putIntArray(RANDOM2, parameterRandomList2.toIntArray())
        super.onSaveInstanceState(outState)
    }
}
