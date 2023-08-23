package com.example.skillcinema.ui.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MovieList(
    @field:Json(name ="total") val total: Int = 0,
    @field:Json(name ="totalPages") val totalPages: Int? = 0,
    @field:Json(name ="items") val items: List<Movie>
)
