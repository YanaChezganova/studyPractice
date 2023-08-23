package com.example.skillcinema.ui.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)

data class MoviePagedList (
    @field:Json(name ="keyword") val keyword: String? = null,
    @field:Json(name ="pagesCount") val pagesCount: Int,
    @field:Json(name ="films") val films: List<Movie>,
    )
