package com.example.sunnyday.ui.search

import android.content.Context
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sunnyday.data.DataRepository
import com.example.sunnyday.data.NetworkRepository
import com.example.sunnyday.data.RoomRepository
import com.example.sunnyday.data.WeatherBaseDao
import com.example.sunnyday.di.RoomModule
import com.example.sunnyday.entity.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class SearchViewModel @Inject constructor(
    val networkRepository: NetworkRepository,
    val dataRepository: DataRepository,
    val roomRepository : RoomRepository
) : ViewModel() {
    private var _state = MutableStateFlow<State>(State.Ready)
    val stateOfView = _state.asStateFlow()
    //@Inject
   //lateinit var roomRepository : RoomRepository
    private val _searchResult = MutableStateFlow<ComplexWeather>(
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
                    _searchResult.value = it
                    _state.value = State.Done
                    Log.d("SVM search result +", " $it")
                    delay(1000)
                },
                onFailure = {
                    _state.value = State.Error
                    Log.d("SVM search result -", " ${it.message} ${it.cause}")
                    delay(1000)
                }
            )
        }
        _state.value = State.Ready
    }

    fun setWeatherToDataBase(weather: ComplexWeather) {
        //   roomRepository = RoomRepository(dao)

        viewModelScope.launch {
            kotlin.runCatching {
                roomRepository.addWeather(weather)
                println("add weather ${weather.location.name}")
            }
        }
    }

    fun allCities(): StateFlow<List<CityDB>> {
       // roomRepository = RoomRepository(dao)
        return   roomRepository.allCities
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = emptyList()
            )
    }


    private fun setCityToDataBase(cityLocation: Location) {
  //  roomRepository = RoomRepository(dao)

        viewModelScope.launch {
            kotlin.runCatching {
                roomRepository.addCity(cityLocation)
                println("add city ${cityLocation.name}")
            }
        }
    }

    fun getWeatherForCityFromDB(city: String): StateFlow<List<WeatherDB>> {
    // roomRepository = RoomRepository(dao)
        return roomRepository.getWeatherForCity(city)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = emptyList()
            )
    }
    fun loadFromSharedPreference(context: Context, key: String): String {
        println("load frOm SH PREF ")
        return dataRepository.loadFromSharedPreference(context, key)
    }

    fun loadToSharedPreference(context: Context, key: String, value: String?) {
        println("ADD TO SH PREF $value")
        dataRepository.loadToSharedPreference(context, key, value)
    }

    suspend fun findCityInDB(cityLocation: Location) {
     //   roomRepository = RoomRepository(dao)
        val listOfCities = roomRepository.allCities.value

        delay(1200)
        var contains = false
        listOfCities.forEach { cityDB ->
            contains =
                cityDB.nameCity == cityLocation.name && cityDB.region == cityLocation.region && cityDB.country == cityLocation.country
        }
        if (!contains) {
            println("not contains ${cityLocation.name}")
            setCityToDataBase(cityLocation)
        }
    }

    suspend fun findWeatherInDB(weather: ComplexWeather) {
   //  roomRepository = RoomRepository(dao)

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
                    weatherDB.nameCity == weather.location.name //&& weatherDB.localTime == weather.location.localtime
                println("contains weather ${weatherDB.nameCity} == ${weather.location.name} contains $contains")
                if (contains) {
                    roomRepository.deleteWeatherForCityFromBase(weatherDB.nameCity)
                    setWeatherToDataBase(weather)
                }
            }
            if (!contains) {
                println("not contains  $contains")
                setWeatherToDataBase(weather)
            }
        }
    }

}