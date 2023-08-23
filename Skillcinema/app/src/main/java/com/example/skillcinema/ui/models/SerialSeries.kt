package com.example.skillcinema.ui.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SerialSeries(
    @field:Json(name ="total") val totalSeasons: Int = 0,
    @field:Json(name ="items") val itemSeason: List<Season>,

    )

@JsonClass(generateAdapter = true)
data class Season(
    @field:Json(name ="number") val number: Int = 0,
    @field:Json(name ="episodes") val episodes: List<Episode>,
    )

@JsonClass(generateAdapter = true)
data class Episode(
    @field:Json(name ="seasonNumber") val seasonNumber: Int = 0,
    @field:Json(name ="episodeNumber") val episodeNumber: Int = 0,
    @field:Json(name ="nameRu") val nameRu: String?,
    @field:Json(name ="nameEn") val nameEn: String?,
    @field:Json(name ="synopsis") val synopsis: String?,
    @field:Json(name ="releaseDate") val releaseDate: String?,
    )

