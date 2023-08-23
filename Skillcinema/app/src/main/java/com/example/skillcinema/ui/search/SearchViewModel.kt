package com.example.skillcinema.ui.search

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.skillcinema.ui.data.*
import com.example.skillcinema.ui.data.Constants.COUNTRY
import com.example.skillcinema.ui.data.Constants.GENRE
import com.example.skillcinema.ui.data.Constants.ORDER
import com.example.skillcinema.ui.data.Constants.RANGE
import com.example.skillcinema.ui.data.Constants.SORT
import com.example.skillcinema.ui.data.Constants.YEAR_SINCE
import com.example.skillcinema.ui.data.Constants.YEAR_UNTIL
import com.example.skillcinema.ui.models.Movie
import com.example.skillcinema.ui.models.State
import com.example.skillcinema.ui.support.MoviePagedSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(val repository: Repository) :
    ViewModel() {  
    private var _state = MutableStateFlow<State>(State.Loading)
    val stateOfView = _state.asStateFlow()
    val listOfGenres: List<Genre> = repository.transformGenres()
    val listOfCountries: List<Country> = repository.transformCountries()
    private val _searchResult = MutableStateFlow<List<Movie>>(emptyList())
    val searchResult = _searchResult.asStateFlow()
    private val _searchBySettings = MutableStateFlow<List<Movie>>(emptyList())
    val searchBySettings = _searchBySettings.asStateFlow()

    @OptIn(ExperimentalPagingApi::class)
    fun pagedSearchBySettingsResult(
        country: Int, genre: Int, order: String,
        type: String,
        ratingFrom: String,
        ratingTo: String, yearFrom: String,
        yearTo: String
    ): Flow<PagingData<Movie>> =
        Pager(config = PagingConfig(pageSize = 20),
            initialKey = null,
            remoteMediator = null,
            pagingSourceFactory = {
                MoviePagedSource(
                    "searchFilmsBySettings", country,
                    genre, "$ratingFrom-$ratingTo+$order*$type#$yearFrom$yearTo",
                    // all this parameters as keyword:  ratingTo, yearFrom, yearTo ..
                )
            }).flow.cachedIn(
            viewModelScope
        )

    @OptIn(ExperimentalPagingApi::class)
    fun pagedSearchResult(keyword: String): Flow<PagingData<Movie>> =
        Pager(config = PagingConfig(pageSize = 20),
            initialKey = null,
            remoteMediator = null,
            pagingSourceFactory = { MoviePagedSource("searchFilms", 0, 0, keyword) }).flow.cachedIn(
            viewModelScope
        )

    fun loadSearchResults(keyword: String, page: Int) {
        _state.value = State.Loading
        viewModelScope.launch {
            kotlin.runCatching {
                repository.getSearchResult(keyword, page)
            }.fold(
                onSuccess = {
                    _state.value = State.Done
                    _searchResult.value = it.films
                },
                onFailure = {
                    _state.value = State.Error
                    Log.d("SVM search result", " ${it.message} ${it.cause}")
                }
            )
        }
    }

    fun loadFromSharedPreference(context: Context, key: String): String {
        return repository.loadFromSharedPreference(context, key)
    }

    fun loadToSharedPreference(context: Context, key: String, value: String?) {
        repository.loadToSharedPreference(context, key, value)
    }

    suspend fun loadSearchResultSettings(
        country: Int, genre: Int, order: String,
        type: String, ratingFrom: String,
        ratingTo: String, yearFrom: String,
        yearTo: String, page: Int
    ) {
        _state.value = State.Loading
        viewModelScope.launch {
            kotlin.runCatching {
                repository.getSearchFilmsBySettings(
                    country, genre, order,
                    type, ratingFrom, ratingTo,
                    yearFrom, yearTo, page
                )
            }.fold(
                onSuccess = {
                    _state.value = State.Done
                    _searchBySettings.value = it.items
                },
                onFailure = {
                    _state.value = State.Error
                    Log.d("SVM loadSearchResultSettings", " ${it.message} ${it.stackTrace}")
                }
            )
        }
    }

    override fun onCleared() {// AppIntro надо оставить KEY
       repository.clearSharedPreference(RoomModule().providesApplicationContext(), COUNTRY)
        repository.clearSharedPreference(RoomModule().providesApplicationContext(), GENRE)
        repository.clearSharedPreference(RoomModule().providesApplicationContext(), YEAR_SINCE)
        repository.clearSharedPreference(RoomModule().providesApplicationContext(), YEAR_UNTIL)
        repository.clearSharedPreference(RoomModule().providesApplicationContext(), RANGE)
        repository.clearSharedPreference(RoomModule().providesApplicationContext(), SORT)
        repository.clearSharedPreference(RoomModule().providesApplicationContext(), ORDER)
        super.onCleared()
    }

}

