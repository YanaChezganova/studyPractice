package com.example.skillcinema.ui.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoList(
    @field:Json(name ="total") val total: Int = 0,
    @field:Json(name ="totalPages") val totalPages: Int = 0,
    @field:Json(name ="items") val items: List<Photo>
)
@JsonClass(generateAdapter = true)
data class Photo(
    @field:Json(name ="imageUrl") val imageUrl: String,
    @field:Json(name ="previewUrl") val previewUrl: String,

    )
