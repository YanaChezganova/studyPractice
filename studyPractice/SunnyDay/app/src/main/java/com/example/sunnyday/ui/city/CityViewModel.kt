package com.example.sunnyday.ui.city

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sunnyday.data.NetworkRepository
import com.example.sunnyday.data.RoomRepository
import com.example.sunnyday.entity.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CityViewModel @Inject constructor(
    val networkRepository: NetworkRepository,
    val roomRepository: RoomRepository,
) : ViewModel() {
    private var _state = MutableStateFlow<State>(State.Ready)
    val stateOfView = _state.asStateFlow()
    private val _searchResult = MutableStateFlow(
        ComplexWeather(
            "false", RequestParameters("", ""),
            Location("", "", "", 0.0, 0.0, "", 0L), CurrentWeather(
                0, listOf(""),
                0, "", 0, 0
            )
        )
    )
    val searchResult = _searchResult.asStateFlow()
    fun loadSearchResults(nameCity: String) {
        _state.value = State.Loading
        viewModelScope.launch {
            kotlin.runCatching {
                networkRepository.getWeather(nameCity)
            }.fold(
                onSuccess = {
                    _state.value = State.Done
                    _searchResult.value = it
                    Log.d("SVM search result +", " $it")

                },
                onFailure = {
                    _state.value = State.Error
                    Log.d("SVM search result -", " ${it.message} ${it.cause}")
                }
            )
        }
    }

    fun allCities(): StateFlow<List<CityDB>> =
        roomRepository.allCities
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = emptyList()
            )

    suspend fun deleteAllFromDataBase() {
        roomRepository.deleteAllFromBase()
    }

    suspend fun findWeatherInDB(weather: ComplexWeather) {
        var listOfWeather = emptyList<WeatherDB>()
        viewModelScope.launch {
            roomRepository.allWeather.collect {
                listOfWeather = it
            }
        }
        viewModelScope.launch {
            delay(800)
            var contains = false
            listOfWeather.forEach { weatherDB ->
                contains =
                    weatherDB.nameCity == weather.location.name && weatherDB.localTime == weather.location.localtime
            }
            if (!contains) {
                setWeatherToDataBase(weather)
            }
        }
    }

    private fun setWeatherToDataBase(weather: ComplexWeather) {
        viewModelScope.launch {
            kotlin.runCatching {
                roomRepository.addWeather(weather)
                println("add weather ${weather.location.name}")
            }
        }
    }
}


