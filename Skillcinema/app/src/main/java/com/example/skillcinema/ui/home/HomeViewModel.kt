package com.example.skillcinema.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.skillcinema.ui.data.*
import com.example.skillcinema.ui.data.Constants.POPULAR
import com.example.skillcinema.ui.data.Constants.RANDOM1
import com.example.skillcinema.ui.data.Constants.RANDOM2
import com.example.skillcinema.ui.data.Constants.SERIAL
import com.example.skillcinema.ui.data.Constants.TOP
import com.example.skillcinema.ui.data.Country
import com.example.skillcinema.ui.data.Genre
import com.example.skillcinema.ui.models.*
import com.example.skillcinema.ui.support.MoviePagedSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val repository: Repository) :
    ViewModel() {
    private val calendar = Calendar.getInstance()
    private var _state = MutableStateFlow<State>(State.Loading)
    val stateOfView = _state.asStateFlow()
    var movieInfo = Movie(
        0, 0, "", "", "", "", "", "",
        countries = listOf(), genres = listOf(), 1.0, 1.0, "", "", 60, "", "",
        "", "", "", "", null, "", ""
    )
    var personInfo = Person(0, 0, "", "", "", "", "", "", "", "", emptyList())

    private val _premieres = MutableStateFlow<List<Movie>>(emptyList())
    val premieres = _premieres.asStateFlow()
    private val _popular = MutableStateFlow<List<Movie>>(emptyList())
    val popular = _popular.asStateFlow()
    val _randomFirstList = MutableStateFlow<List<Movie>>(emptyList())
    val randomFirstList = _randomFirstList.asStateFlow()
    private val _topList = MutableStateFlow<List<Movie>>(emptyList())
    val topList = _topList.asStateFlow()
    val _randomSecondList = MutableStateFlow<List<Movie>>(emptyList())
    val randomSecondList = _randomSecondList.asStateFlow()
    val listOfGenres: List<Genre> = repository.transformGenres()
    val listOfCountries: List<Country> = repository.transformCountries()
    private val _serials = MutableStateFlow<List<Movie>>(emptyList())
    val serials = _serials.asStateFlow()
    private val _staffActor = MutableStateFlow<List<Person>>(emptyList())
    val staffActor = _staffActor.asStateFlow()
    private val _staffOther = MutableStateFlow<List<Person>>(emptyList())
    val staffOther = _staffOther.asStateFlow()
    private val _photo = MutableStateFlow<List<Photo>>(emptyList())
    val photo = _photo.asStateFlow()
    private val _similar = MutableStateFlow<List<Movie>>(emptyList())
    val similar = _similar.asStateFlow()
    private val _poster = MutableStateFlow<List<Photo>>(emptyList())
    val poster = _poster.asStateFlow()
    private val _serialSeriesInfo = MutableStateFlow<List<Season>>(emptyList())
    val serialSeriesInfo = _serialSeriesInfo.asStateFlow()

    @OptIn(ExperimentalPagingApi::class)
    val pagedTopMovies: Flow<PagingData<Movie>> = Pager(config = PagingConfig(pageSize = 20),
        initialKey = null,
        remoteMediator = null,
        pagingSourceFactory = { MoviePagedSource(TOP, 0, 0, "") }).flow.cachedIn(viewModelScope)

    @OptIn(ExperimentalPagingApi::class)
    val pagedPopularMovies: Flow<PagingData<Movie>> = Pager(config = PagingConfig(pageSize = 20),
        initialKey = null,
        remoteMediator = null,
        pagingSourceFactory = { MoviePagedSource(POPULAR, 0, 0, "") }).flow.cachedIn(
        viewModelScope
    )

    @OptIn(ExperimentalPagingApi::class)
    val pagedSerialMovies: Flow<PagingData<Movie>> = Pager(config = PagingConfig(pageSize = 20),
        initialKey = null,
        remoteMediator = null,
        pagingSourceFactory = {
            MoviePagedSource(
                SERIAL,
                0,
                0,
                ""
            )
        }).flow.cachedIn(viewModelScope)

    @OptIn(ExperimentalPagingApi::class)
    fun pagedRandom1(countryId: Int, genreId: Int): Flow<PagingData<Movie>> =
        Pager(config = PagingConfig(pageSize = 20),
            initialKey = null,
            remoteMediator = null,
            pagingSourceFactory = {
                MoviePagedSource(
                    RANDOM1,
                    countryId,
                    genreId,
                    ""
                )
            }).flow.cachedIn(viewModelScope)

    @OptIn(ExperimentalPagingApi::class)
    fun pagedRandom2(countryId: Int, genreId: Int): Flow<PagingData<Movie>> =
        Pager(config = PagingConfig(pageSize = 20),
            initialKey = null,
            remoteMediator = null,
            pagingSourceFactory = {
                MoviePagedSource(
                    RANDOM2,
                    countryId,
                    genreId,
                    ""
                )
            }).flow.cachedIn(viewModelScope)

    @OptIn(ExperimentalPagingApi::class)

    private val month =
        calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.forLanguageTag("en"))
            ?.uppercase()
    private val year = calendar.get(Calendar.YEAR)

    fun loadPremieres() {
        viewModelScope.launch {
            kotlin.runCatching {
                repository.getPremieres(year, month!!)
            }.fold(
                onSuccess = { _premieres.value = it.items },
                onFailure = {
                    Log.d("HVM loadPremieres", " ${it.message} ${it.cause}")
                }
            )
        }
    }

    fun loadPopular(page: Int) {
        viewModelScope.launch {
            kotlin.runCatching {
                repository.getPopular(page)
            }.fold(
                onSuccess = {
                    _popular.value = it.films
                },
                onFailure = {
                    Log.d("HVM popular", " ${it.message} ${it.cause}")
                }
            )
        }
    }

    fun getRandomListOfFilms(
        countryId: Int,
        genreId: Int,
        flow: MutableStateFlow<List<Movie>>,
        page: Int
    ) {
        viewModelScope.launch {
            kotlin.runCatching {
                repository.getRandomListOfFilms(countryId, genreId, page)
            }.fold(
                onSuccess = { flow.value = it.items },
                onFailure = { Log.d("HVM RandomListOfFilms", " ${it.message} ${it.stackTrace}") }
            )
        }
    }

    fun loadTopList(page: Int) {
        viewModelScope.launch {
            kotlin.runCatching {
                repository.getTopList(page)
            }.fold(
                onSuccess = { _topList.value = it.films },
                onFailure = { Log.d("HVM loadTopList", " ${it.message} ${it.cause}") }
            )
        }
    }

    fun loadSerials(page: Int) {
        viewModelScope.launch {
            kotlin.runCatching {
                repository.getSerials(page)
            }.fold(
                onSuccess = { _serials.value = it.items },
                onFailure = { Log.d("HVM serials", " ${it.message} ${it.cause}") }
            )
        }
    }

    fun loadMovieInformation(movieId: Int) {
        viewModelScope.launch {
            kotlin.runCatching {
                repository.getMovieInformation(movieId)
            }.fold(
                onSuccess = { movieInfo = it },
                onFailure = { Log.d("HVM MovieInfo", " ${it.message} ${it.cause}") }
            )
        }
    }

    fun loadMovieStaff(movieId: Int) {
        val listActor = mutableListOf<Person>()
        val listOther = mutableListOf<Person>()

        viewModelScope.launch {
            kotlin.runCatching {
                repository.getMovieStaff(movieId)
            }.fold(
                onSuccess = {
                    it.onEach { person ->
                        if (person.professionKey == "ACTOR")
                            listActor.add(person)
                        else listOther.add(person)
                    }
                    _staffActor.value = listActor
                    _staffOther.value = listOther
                },
                onFailure = { Log.d("HVM MovieStaff", " ${it.message} ${it.cause}") }
            )
        }
    }

    fun loadSimilarFilms(movieId: Int) {
        viewModelScope.launch {
            kotlin.runCatching {
                repository.getSimilarFilms(movieId)
            }.fold(
                onSuccess = {
                    _similar.value = it.items
                },
                onFailure = { Log.d("HVM Similar", " ${it.message} ${it.cause}") }
            )
        }
    }

    suspend fun loadPoster(movieId: Int, type: String, page: Int): Int {
        _state.value = State.Loading
        val totalDeferred = viewModelScope.async {
            val total = kotlin.runCatching {
                repository.getPoster(movieId, type, page)
            }.fold(
                onSuccess = {
                    _poster.value = it.items
                    _state.value = State.Done
                    return@fold it.total
                },
                onFailure = {
                    _state.value = State.Error
                    Log.d("HVM Poster", " ${it.message} ${it.cause}")
                }
            )
            return@async total
        }
        return kotlin.run { totalDeferred.await() }
    }

    suspend fun loadSerialSeriesInfo(movieId: Int): Int {
        val totalDeferred = viewModelScope.async {
            val total = kotlin.runCatching {
                repository.getSerialSeries(movieId)
            }.fold(
                onSuccess = {
                    _serialSeriesInfo.value = it.itemSeason
                    return@fold it.totalSeasons
                },
                onFailure = { Log.d("HVM Season Info", " ${it.message} ${it.cause}") }
            )
            return@async total
        }
        return kotlin.run { totalDeferred.await() }
    }

    fun loadPersonInformation(personId: Int) {
        _state.value = State.Loading
        viewModelScope.launch {
            kotlin.runCatching {
                repository.getPersonInfo(personId)
            }.fold(
                onSuccess = {
                    personInfo = it
                    _state.value = State.Done
                },
                onFailure = {
                    _state.value = State.Error
                    Log.d("HVM PersonInfo", " ${it.message} ${it.cause}")
                }
            )
        }
    }

}