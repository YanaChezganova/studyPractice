package com.example.skillcinema.ui.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)

data class Movie(
    @field:Json(name ="kinopoiskId") val kinopoiskId: Int = 0,
    @field:Json(name ="filmId") val filmId: Int = 0,
    @field:Json(name ="nameRu") val nameRu: String?,
    @field:Json(name ="nameEn") val nameEn: String?,
    @field:Json(name ="nameOriginal") val nameOriginal: String? = "нет данных",
    @field:Json(name ="year") val year: String? = "нет данных",
    @field:Json(name ="filmLength") val filmLength: String? = "нет данных",
    @field:Json(name ="posterUrl") val posterUrl: String?,
    @field:Json(name ="countries") val countries: List<Country>?,
    @field:Json(name ="genres") val genres: List<Genre>? = listOf(Genre(""),Genre("")),
    @field:Json(name ="ratingKinopoisk") val ratingKinopoisk: Double?,
    @field:Json(name ="ratingImdb") val ratingImdb: Double?,
    @field:Json(name ="webUrl") val webUrlFilm: String? = null,
    @field:Json(name ="rating") val rating: String?,
    @field:Json(name ="duration") val duration: Int? = 60,
    @field:Json(name ="premiereRu") val premiereRu: String? = "нет данных",
    @field:Json(name ="slogan") val slogan: String? = "нет данных",
    @field:Json(name ="description") val description: String?,
    @field:Json(name ="shortDescription") val shortDescription: String?,
    @field:Json(name ="startYear") val startYear: String?,
    @field:Json(name ="endYear") val endYear: String?,
    @field:Json(name ="serial") val serial: Boolean?,
    @field:Json(name ="ratingAgeLimits") val ratingAgeLimits: String?,
    @field:Json(name ="professionKey") val professionKey: String?,
    )
@JsonClass(generateAdapter = true)
data class Country(
    @field:Json(name ="country") val country: String
)

@JsonClass(generateAdapter = true)
data class Genre(
    @field:Json(name ="genre") val genres: String
    )
