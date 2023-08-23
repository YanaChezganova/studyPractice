package com.example.skillcinema.ui.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Person(
    @field:Json(name ="staffId") val staffId: Int = 0,
    @field:Json(name ="personId") val personId: Int = 0,
    @field:Json(name ="nameRu") val nameRu: String?,
    @field:Json(name ="nameEn") val nameEn: String?,
    @field:Json(name ="sex") val sex: String?,
    @field:Json(name ="description") val description: String?,
    @field:Json(name ="posterUrl") val posterUrl: String?,
    @field:Json(name ="professionText") val professionText: String?,
    @field:Json(name ="professionKey") val professionKey: String?,
    @field:Json(name ="profession") val profession: String?,
    @field:Json(name ="films") val films: List<Movie> = emptyList<Movie>()
)
