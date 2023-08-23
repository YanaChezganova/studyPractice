package com.example.skillcinema.ui.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Genres(
    @Json(name = "genres") val genres: List<Genre>)
@JsonClass(generateAdapter = true)
    data class Genre(
    @Json(name = "id") val id: Int,
    @Json(name = "genre")val genre: String)

@JsonClass(generateAdapter = true)
data class Countries(
    @Json(name = "countries") val countries: List<Country>)

@JsonClass(generateAdapter = true)
data class Country(
    @Json(name = "id") val id: Int,
    @Json(name = "country") val country: String)



