package com.example.skillcinema.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.R
import com.example.skillcinema.databinding.CardOfFilmBinding
import com.example.skillcinema.databinding.GalleryBigImageBinding
import com.example.skillcinema.ui.data.*
import com.example.skillcinema.ui.models.Movie
import com.example.skillcinema.ui.models.Person
import com.example.skillcinema.ui.models.Photo
import com.example.skillcinema.ui.models.State
import com.example.skillcinema.ui.support.AdapterRecyclerViewWithWatchedFilms
import com.example.skillcinema.ui.support.PersonAdapterRecyclerView
import com.example.skillcinema.ui.support.PhotoAdapterRecyclerView
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import com.ms.square.android.expandabletextview.ExpandableTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.lang.IndexOutOfBoundsException
import javax.inject.Inject
import com.example.skillcinema.ui.data.Constants.FILM_ID
import com.example.skillcinema.ui.data.Constants.HEADER
import com.example.skillcinema.ui.data.Constants.PERSON_ID
import com.example.skillcinema.ui.data.Constants.PROFESSION
import com.example.skillcinema.ui.data.Constants.URL_IMAGE
import com.example.skillcinema.ui.data.Constants.RATING
import com.example.skillcinema.ui.data.Constants.NAME
import com.example.skillcinema.ui.data.Constants.PAGED
import com.example.skillcinema.ui.data.Constants.REQUEST_ID
import com.example.skillcinema.ui.data.Constants.TYPE

@AndroidEntryPoint
class CardOfFilm : Fragment() {
    private var _binding: CardOfFilmBinding? = null
    private val binding get() = _binding!!
    private var bundle = Bundle()
    private var filmId: Int = 0
    private lateinit var personActorsRecyclerView: RecyclerView
    private lateinit var personOtherRecycleView: RecyclerView
    private lateinit var photoRecycleView: RecyclerView
    private lateinit var similarFilmsRecyclerView: RecyclerView

    @Inject
    lateinit var personActorsAdapter: PersonAdapterRecyclerView

    @Inject
    lateinit var personOtherAdapter: PersonAdapterRecyclerView

    @Inject
    lateinit var photoAdapter: PhotoAdapterRecyclerView

    @Inject
    lateinit var similarFilmsAdapter: AdapterRecyclerViewWithWatchedFilms

    @Inject
    lateinit var roomRepository: RoomRepository
    val viewModel: HomeViewModel by viewModels()

    private var headerOfFilm: String = ""
    private var ratingOfFilm: String = ""
    private var filmInFavoriteFolder = false
    private var filmInWantToSeeFolder = false
    private var filmIsWatched = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CardOfFilmBinding.inflate(inflater, container, false)
        binding.progressBar.visibility = View.VISIBLE
        personActorsAdapter = PersonAdapterRecyclerView()
        personOtherAdapter = PersonAdapterRecyclerView()
        photoAdapter = PhotoAdapterRecyclerView()
        similarFilmsAdapter = AdapterRecyclerViewWithWatchedFilms()
        roomRepository =
            RoomRepository(RoomModule().providesDao(RoomModule().providesRoomDatabase(requireContext())))
        arguments?.let {
            filmId = it.getInt(FILM_ID)
        }
        val scope = CoroutineScope(Dispatchers.Default)
        var listOfWatchedFilms = listOf<FilmDB>()
        scope.launch {
            roomRepository.listOfWatchedFilms()
                .collect {
                    listOfWatchedFilms = it
                }
        }

        viewModel.loadMovieInformation(filmId)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            val filmIsInBase = roomRepository.findFilmInBase(filmId)

            viewModel.stateOfView.collect { state ->
                when (state) {
                    State.Loading -> {
                        binding.buttonDots.isEnabled = false
                        binding.buttonFlag.isEnabled = false
                        binding.buttonHeart.isEnabled = false
                        binding.buttonEye.isEnabled = false
                        binding.buttonShare.isEnabled = false
                        binding.progressBar.isVisible = true
                    }
                    State.Done -> {
                        binding.buttonDots.isEnabled = true
                        binding.buttonFlag.isEnabled = true
                        binding.buttonHeart.isEnabled = true
                        binding.buttonEye.isEnabled = true
                        binding.buttonShare.isEnabled = true
                        binding.progressBar.isVisible = false
                        headerOfFilm = checkTitle()
                        ratingOfFilm = checkRating()
                        binding.rangeTitleFilm.text = resources.getString(
                            R.string.rating_title, checkRating(),
                            checkTitle()
                        )

                        binding.yearGenresFilm.text = resources.getString(
                            R.string.film_year_genre_genre, checkYear(),
                            checkGenre()
                        )
                        binding.countryLongRestrictionsFilm.text = resources.getString(
                            R.string.film_country_duration_ageLimit, checkCountry(),
                            checkTime(), checkAgeLimit()
                        )
                        binding.titleOriginal.text =
                            resources.getString(R.string.title_film, checkOriginTitle())
                        Glide.with(binding.poster.context)
                            .load(viewModel.movieInfo.posterUrl)
                            .into(binding.poster)

                        when (filmIsInBase) {
                            true -> {
                                filmIsWatched = roomRepository.checkFilmIsWatched(filmId)
                                filmInFavoriteFolder = roomRepository.findFilmInFolder(filmId, 0)
                                filmInWantToSeeFolder = roomRepository.findFilmInFolder(filmId, 1)
                                delay(100)
                                if (filmIsWatched) binding.buttonEye.setImageResource(R.drawable.ic_eye_white)
                                if (filmInFavoriteFolder) binding.buttonHeart.setImageResource(R.drawable.ic_heart_white)
                                if (filmInWantToSeeFolder) binding.buttonFlag.setImageResource(R.drawable.ic_flag_white)
                            }
                            else -> {
                                val yearGenresFilmText =
                                    if (binding.yearGenresFilm.text.toString().isNotEmpty()) {
                                        if (binding.yearGenresFilm.text.toString().last() == ' ')
                                            binding.yearGenresFilm.text.toString().dropLast(1)
                                        else {
                                            if (binding.yearGenresFilm.text.toString()
                                                    .last() == ','
                                            ) binding.yearGenresFilm.text.toString()
                                                .dropLast(1) else binding.yearGenresFilm.text.toString()
                                        }
                                    } else {
                                        " "
                                    }
                                roomRepository.addFilmInBase(
                                    filmId, false, viewModel.movieInfo.posterUrl,
                                    headerOfFilm, ratingOfFilm, yearGenresFilmText, checkCountry()
                                )
                            }
                        }

                    }
                    else -> {
                        binding.buttonDots.isEnabled = false
                        binding.buttonFlag.isEnabled = false
                        binding.buttonHeart.isEnabled = false
                        binding.buttonEye.isEnabled = false
                        binding.buttonShare.isEnabled = false
                        bundle = Bundle().apply {
                            putInt(FILM_ID, filmId)
                        }
                        findNavController().navigate(R.id.action_global_cardOfFilm, bundle)
                    }
                }
            }
        }
        // button Share
        binding.buttonShare.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, viewModel.movieInfo.webUrlFilm)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Поделиться фильмом")
            startActivity(shareIntent)
        }
        // check film as FAVORITE
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            // Film description

            binding.titleTextInfo.text = checkContentDescription()
            binding.expandTextView.text = viewModel.movieInfo.description
            // Header for Serial
            val serialMark: Boolean = if (viewModel.movieInfo.serial == null) false
            else viewModel.movieInfo.serial!!
            if (serialMark) {
                binding.layoutHeaderSeasonAndSeries.visibility = View.VISIBLE
                val countSeasons = viewModel.loadSerialSeriesInfo(filmId)
                var countEpisodesInSeason = 0
                viewModel.serialSeriesInfo.value.onEach {
                    countEpisodesInSeason += it.episodes.size
                }
                val text = resources.getQuantityString(
                    R.plurals.number_season,
                    countSeasons, countSeasons
                ) + " " + resources.getQuantityString(
                    R.plurals.count_series,
                    countEpisodesInSeason, countEpisodesInSeason
                )
                binding.textSeasonAndSeries.text = text

                binding.buttonAllSeries.setOnClickListener {
                    bundle = Bundle().apply {
                        putInt(FILM_ID, filmId)
                        putString(HEADER, headerOfFilm)
                    }
                    findNavController().navigate(R.id.action_global_serials, bundle)
                }
            }
        }
        binding.buttonEye.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                filmIsWatched = roomRepository.checkFilmIsWatched(filmId)
                delay(200)
                if (filmIsWatched) {
                    binding.buttonEye.setImageResource(R.drawable.ic_not_eye_little)
                    roomRepository.markFilmWatched(filmId, false)
                } else {
                    roomRepository.markFilmWatched(filmId, true)
                    binding.buttonEye.setImageResource(R.drawable.ic_eye_white)
                }
            }
        }
        binding.buttonHeart.setOnClickListener {
            lifecycleScope.launchWhenCreated {
                filmInFavoriteFolder = roomRepository.findFilmInFolder(filmId, 0)
                delay(100)
                if (filmInFavoriteFolder) {
                    binding.buttonHeart.setImageResource(R.drawable.ic_heart_little)
                    roomRepository.deleteFilmFromFolder(filmId, 0)
                } else {
                    roomRepository.addFilmInFolder(filmId, 0)
                    binding.buttonHeart.setImageResource(R.drawable.ic_heart_white)
                }
            }
        }
        binding.buttonFlag.setOnClickListener {
            lifecycleScope.launchWhenCreated {
                filmInWantToSeeFolder = roomRepository.findFilmInFolder(filmId, 1)
                delay(100)
                if (filmInWantToSeeFolder) {
                    binding.buttonFlag.setImageResource(R.drawable.ic_flag_little)
                    roomRepository.deleteFilmFromFolder(filmId, 1)
                } else {
                    roomRepository.addFilmInFolder(filmId, 1)
                    binding.buttonFlag.setImageResource(R.drawable.ic_flag_white)
                }
            }
        }
        //Info about actors
        personActorsRecyclerView = binding.gridActorsRecyclerView
        personActorsRecyclerView.adapter = personActorsAdapter
        personActorsRecyclerView.layoutManager =
            GridLayoutManager(context, 4, GridLayoutManager.HORIZONTAL, false)
        viewModel.loadMovieStaff(filmId)
        viewModel.staffActor.onEach {
            personActorsAdapter.setData(it, ::onPersonClick)
            binding.textCountOfActors.text = personActorsAdapter.dataSize.toString()
            binding.buttonCountOfActors.setOnClickListener {
                openListOfPersons("ACTORS", resources.getString(R.string.starred_in_the_film))
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        //Info about other staff
        personOtherRecycleView = binding.gridWorkersRecyclerView
        personOtherRecycleView.adapter = personOtherAdapter
        personOtherRecycleView.layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
        viewModel.staffOther.onEach {
            personOtherAdapter.setData(it, ::onPersonClick)
            binding.textCountOfFilmWorkers.text = personOtherAdapter.dataSize.toString()
            binding.buttonCountOfFilmWorkers.setOnClickListener {
                openListOfPersons("STAFF", resources.getString(R.string.worked_in_the_film))
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
        // Photo Gallery
        binding.galleryPhotoViewGroup.setHeader(resources.getString(R.string.gallery))
        binding.galleryPhotoViewGroup.setBlueArrowVisible()
        binding.galleryPhotoViewGroup.binding.buttonAllElements.setOnClickListener {
            bundle = Bundle().apply {
                putInt(FILM_ID, filmId)
            }
            findNavController().navigate(R.id.action_global_gallery1, bundle)
        }
        photoRecycleView = binding.galleryPhotoViewGroup.binding.viewGroupHorizontalRecyclerView
        photoRecycleView.adapter = photoAdapter
        lifecycleScope.launch {
            viewModel.loadPoster(filmId, "SHOOTING", 1)
        }
        if (viewModel.poster.value.isNotEmpty()) {
            viewModel.poster.onEach {
                photoAdapter.setData(it, ::onPhotoClick)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        } else {
            lifecycleScope.launch {
                viewModel.loadPoster(filmId, "POSTER", 1)
            }
            viewModel.poster.onEach {
                photoAdapter.setData(it, ::onPhotoClick)
                //сделать по-страничную загр и соотв умножить размер
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        }
        // Similar Films
        viewModel.loadSimilarFilms(filmId)
        binding.similarFilmsViewGroup.setHeader(resources.getString(R.string.similar_films))
        binding.similarFilmsViewGroup.setBlueArrowVisible()
        binding.similarFilmsViewGroup.binding.buttonAllElements.setOnClickListener {
            bundle = Bundle().apply {
                putInt(FILM_ID, filmId)
                putString(REQUEST_ID, "similar")
                putString(HEADER, resources.getString(R.string.similar_films))
                putBoolean(PAGED, false)
            }
            findNavController().navigate(R.id.action_global_listOfFilms, bundle)
        }
        similarFilmsRecyclerView =
            binding.similarFilmsViewGroup.binding.viewGroupHorizontalRecyclerView
        similarFilmsRecyclerView.adapter = similarFilmsAdapter
        viewModel.similar.onEach {
            val listForAdapter =
                makeListOfWatchedFilmsForRecyclerViewAdapter(it, listOfWatchedFilms)
            similarFilmsAdapter.setData(it, listForAdapter, ::onItemClick)
            binding.similarFilmsViewGroup.setCountOfElements(it.size)
            if (it.isEmpty()) {
                binding.similarFilmsViewGroup.visibility = View.GONE
            } else {
                binding.similarFilmsViewGroup.visibility = View.VISIBLE
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        binding.buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_global_navigation_home, null)
        }
        binding.buttonDots.setOnClickListener {
            bundle = Bundle().apply {
                putInt(FILM_ID, filmId)
                putString(URL_IMAGE, viewModel.movieInfo.posterUrl)
                putString(HEADER, headerOfFilm)
                putString("yearAndGenre", binding.yearGenresFilm.text.toString())
                putString(RATING, ratingOfFilm)
            }
            findNavController().navigate(R.id.action_global_bottomFragmentAddToCollection, bundle)
        }

        return binding.root
    }

    private fun checkRating(): String {
        val rating = if (viewModel.movieInfo.rating !== null) viewModel.movieInfo.rating.toString()
        else {
            if (viewModel.movieInfo.ratingKinopoisk !== null) viewModel.movieInfo.ratingKinopoisk.toString()
            else {
                if (viewModel.movieInfo.ratingImdb !== null) viewModel.movieInfo.ratingImdb.toString()
                else "1.0"
            }
        }
        return rating
    }

    private suspend fun checkTitle(): String {
        delay(100)
        val title = if (viewModel.movieInfo.nameRu != null) viewModel.movieInfo.nameRu!!
        else {
            if (viewModel.movieInfo.nameEn != null) viewModel.movieInfo.nameEn!!
            else checkOriginTitle()
        }
        return title
    }

    private fun checkOriginTitle(): String {
        return if (viewModel.movieInfo.nameOriginal != null) viewModel.movieInfo.nameOriginal!!
        else {
            if (viewModel.movieInfo.nameEn != null) viewModel.movieInfo.nameEn!!
            else ""
        }
    }

    private suspend fun checkYear(): String {
        delay(100)
        var year = ""
        if (viewModel.movieInfo.year !== null) year = viewModel.movieInfo.year!!
        else {
            if (viewModel.movieInfo.startYear !== null) year = viewModel.movieInfo.startYear!!
            else {
                if (viewModel.movieInfo.endYear !== null) year = viewModel.movieInfo.endYear!!
            }
        }
        return year
    }

    private suspend fun checkGenre(): String {
        delay(100)
        var genre1 = " "
        var genre2 = " "
        try {
            if (viewModel.movieInfo.genres?.size!! > 1) {
                genre1 = viewModel.movieInfo.genres!![0].genres
                genre2 = viewModel.movieInfo.genres!![1].genres
            } else genre1 = viewModel.movieInfo.genres!![0].genres
        } catch (e: IndexOutOfBoundsException) {
            findNavController().navigate(R.id.action_global_bottomFragmentMistake)
            delay(600)
            bundle = Bundle().apply {
                putInt(FILM_ID, filmId)
            }
            findNavController().navigate(R.id.action_global_cardOfFilm, bundle)
        }
        return if (viewModel.movieInfo.genres?.size!! == 0) " " else "$genre1, $genre2"
    }

    private suspend fun checkCountry(): String {
        delay(100)
        var country = ""
        try {
            country = viewModel.movieInfo.countries?.get(0)?.country!!
        } catch (t: Throwable) {
            findNavController().navigate(R.id.action_global_bottomFragmentMistake)
            delay(600)
            bundle = Bundle().apply {
                putInt(FILM_ID, filmId)
            }
            findNavController().navigate(R.id.action_global_cardOfFilm, bundle)
        }

        return country
    }

    private suspend fun checkTime(): String {
        delay(100)
        var allTime: String? = "80"
        try {
            allTime = viewModel.movieInfo.filmLength.toString()
        } catch (t: Throwable) {
            findNavController().navigate(R.id.action_global_bottomFragmentMistake)
            delay(600)
            bundle = Bundle().apply {
                putInt(FILM_ID, filmId)
            }
            findNavController().navigate(R.id.action_global_cardOfFilm, bundle)
        }
        val hours: Int
        val minutes: Int
        if (allTime != null && allTime != "" && allTime != "null") {
            hours = allTime.toInt().div(60)
            minutes = allTime.toInt().minus(hours.times(60))
        } else {
            hours = 1
            minutes = 20
        }
        return resources.getString(R.string.hours_minutes, hours.toString(), minutes.toString())
    }

    private suspend fun checkAgeLimit(): String {
        // delay(100)
        var text = ""
        kotlin.runCatching {
            text =
                if (viewModel.movieInfo.ratingAgeLimits != null && viewModel.movieInfo.ratingAgeLimits!!.isNotEmpty())
                    "${viewModel.movieInfo.ratingAgeLimits!!.substring(3)}+"
                else "16+"
        }
            .onFailure {
                findNavController().navigate(R.id.action_global_bottomFragmentMistake)
                delay(600)
                bundle = Bundle().apply {
                    putInt(FILM_ID, filmId)
                }
                findNavController().navigate(R.id.action_global_cardOfFilm, bundle)
            }
        return text
    }

    private suspend fun checkContentDescription(): String {
        // delay(100)
        return if (viewModel.movieInfo.shortDescription != null) viewModel.movieInfo.shortDescription!!
        else checkTitle()
    }

    private fun onItemClick(item: Movie) {
        if (item.kinopoiskId > item.filmId)
            bundle = Bundle().apply {
                putInt(FILM_ID, item.kinopoiskId)
            }
        else bundle = Bundle().apply {
            putInt(FILM_ID, item.filmId)
        }
        findNavController().navigate(R.id.action_global_cardOfFilm, bundle)
    }

    private fun onPhotoClick(item: Photo) {
        bundle = Bundle().apply {
            putString(URL_IMAGE, item.imageUrl)
        }
        findNavController().navigate(R.id.action_global_galleryBigPhoto, bundle)
    }

    private fun onPersonClick(item: Person) {
        bundle = Bundle().apply {
            if (item.staffId > item.personId)
                putInt(PERSON_ID, item.staffId)
            else putInt(PERSON_ID, item.personId)
            if (item.nameRu != null)
                putString(NAME, item.nameRu)
            else putString(NAME, item.nameEn)
            putString(URL_IMAGE, item.posterUrl)
            putInt(FILM_ID, filmId)
            val profession = if (item.professionText?.last()?.compareTo('ы') == 0) {
                item.professionText.dropLast(1)
            } else {
                item.professionText!!
            }
            putString(PROFESSION, profession)
        }
        findNavController().navigate(R.id.action_global_infoActor, bundle)
    }

    private fun openListOfPersons(type: String, header: String) {
        bundle = Bundle().apply {
            putInt(FILM_ID, filmId)
            putString(TYPE, type)
            putString(HEADER, header)
        }
        findNavController().navigate(R.id.action_global_listOfPersons, bundle)
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
    override fun onDestroyView() {
        _binding = null
        personActorsRecyclerView.adapter = null
        personOtherRecycleView.adapter = null
        photoRecycleView.adapter = null
        similarFilmsRecyclerView.adapter = null
        super.onDestroyView()

    }
}