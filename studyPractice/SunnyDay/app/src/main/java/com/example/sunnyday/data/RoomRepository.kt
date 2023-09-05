package com.example.sunnyday.data

import com.example.sunnyday.entity.CityDB
import com.example.sunnyday.entity.ComplexWeather
import com.example.sunnyday.entity.Location
import com.example.sunnyday.entity.WeatherDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


class RoomRepository @Inject constructor(private val dao: WeatherBaseDao) {

    private val scope = CoroutineScope(Dispatchers.Default)

    val allWeather: StateFlow<List<WeatherDB>> = this.dao.getAllWeather()
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )
    val allCities: StateFlow<List<CityDB>> = this.dao.getAllCities()
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

  fun getWeatherForCity(nameCity: String): StateFlow<List<WeatherDB>> =
      this.dao.getWeatherForCity(nameCity)
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = emptyList()
            )

    suspend fun addWeather(weather: ComplexWeather) {
        scope.launch {
            dao.insertWeatherDB(
                WeatherDB(
                    System.currentTimeMillis().toInt(),
                    nameCity = weather.location.name!!,
                    localTimeInMillis = weather.location.localtime_epoch!!,
                    localTime = weather.location.localtime!!,
                    temperature = weather.current.temperature!!,
                    weatherIconUrl = weather.current.weather_icons.first()!!,
                    windSpeed = weather.current.wind_speed!!,
                    windDirection = weather.current.wind_dir!!,
                    humidity = weather.current.humidity!!,
                    cloudCover = weather.current.cloudcover!!
                )
            )
            println("weather added!")
        }
    }

    suspend fun addCity(aboutCity: Location) {
        val size = allCities.value.size
        println("size add city = $size")
        scope.launch {
            dao.insertCtyDB(
                CityDB(
                    size, aboutCity.name!!, aboutCity.country!!, aboutCity.region!!,
                    aboutCity.lat!!, aboutCity.lon!!
                )
            )
            println("city added!")

        }
    }
    suspend fun deleteAllFromBase(){
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch { dao.deleteAllCity()
            dao.deleteAllWeather()
        }
    }

    suspend fun deleteWeatherForCityFromBase(city: String){
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            dao.deleteWeatherForCity(city)
            println("deleted weather for $city")
        }
    }
}

