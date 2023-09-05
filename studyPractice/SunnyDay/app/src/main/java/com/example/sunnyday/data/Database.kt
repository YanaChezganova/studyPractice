package com.example.sunnyday.data

import androidx.room.*
import androidx.room.Database
import com.example.sunnyday.entity.CityDB
import com.example.sunnyday.entity.WeatherDB
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow


@Database(entities =
    [CityDB::class,
     WeatherDB::class,],
      version = 1)

abstract class SunnyDayDataBase: RoomDatabase() {
    abstract fun sunnyDayBaseDao(): WeatherBaseDao
}

@Dao
interface WeatherBaseDao {
    @Transaction
    @Query("SELECT * FROM weather WHERE nameCity LIKE :nameCity")
    fun getWeatherForCity(nameCity: String): Flow<List<WeatherDB>>

    @Insert(entity = CityDB::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCtyDB(city: CityDB)

    @Insert(entity = WeatherDB::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherDB(weather: WeatherDB)

    @Query("SELECT * FROM weather")
    fun getAllWeather(): Flow<List<WeatherDB>>

    @Query("SELECT * FROM City")
    fun getAllCities(): Flow<List<CityDB>>

    @Transaction
    @Query("DELETE FROM weather")
    suspend fun deleteAllWeather()

    @Transaction
    @Query("DELETE FROM city")
    suspend fun deleteAllCity()

    @Transaction
    @Query("DELETE FROM weather WHERE nameCity LIKE :nameCity")
    suspend fun deleteWeatherForCity(nameCity: String)
}
