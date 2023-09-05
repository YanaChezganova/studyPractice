package com.example.sunnyday.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "City")
data class CityDB (

    @ColumnInfo(name = "id")
    val id: Int,

    @PrimaryKey
    @ColumnInfo(name = "nameCity")
    val nameCity: String,

    @ColumnInfo(name = "country")
    val country: String,

    @ColumnInfo(name = "region")
    val region: String?,

    @ColumnInfo(name = "lat")
    val lat: Double,

    @ColumnInfo(name = "lon")
    val lon: Double,
)

@Entity(tableName = "weather")
data class WeatherDB  (
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "nameCity")
    val nameCity: String,

    @ColumnInfo(name = "local_Time_In_Millis")
    val localTimeInMillis: Long,

    @ColumnInfo(name = "local_Time")
    val localTime: String,

    @ColumnInfo(name = "temperature")
    val temperature: Int,

    @ColumnInfo(name = "weather_Icon_Url")
    val weatherIconUrl: String,

    @ColumnInfo(name = "wind_Speed_m_in_sec")
    val windSpeed: Int,

    @ColumnInfo(name = "wind_Direction")
    val windDirection: String,

    @ColumnInfo(name = "humidity_perc")
    val humidity: Int,

    @ColumnInfo(name = "cloud_cover_perc")
    val cloudCover: Int,
)


