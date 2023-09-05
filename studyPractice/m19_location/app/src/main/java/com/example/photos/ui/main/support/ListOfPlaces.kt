package com.example.photos.ui.main.support

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ListOfPlaces(
    @field:Json(name = "type") val type: String,
    @field:Json(name = "features") val listOfPlaces: List<PlaceOfInterestShotInfo>,
)

@JsonClass(generateAdapter = true)
data class PlaceOfInterestShotInfo(
    @field:Json(name = "type") val type: String,
    @field:Json(name = "id") val id: String,
    @field:Json(name = "geometry") val geometry: Geometry,
    @field:Json(name = "properties") val properties: Properties,
)

@JsonClass(generateAdapter = true)
data class Geometry(
    @field:Json(name = "type") val type: String,
    @field:Json(name = "coordinates") val coordinates: List<Double>,
)

@JsonClass(generateAdapter = true)
data class Properties(
    @field:Json(name = "xid") val xid: String,
    @field:Json(name = "name") val nameOfPlace: String,
    @field:Json(name = "dist") val distance: String,
    @field:Json(name = "rate") val rate: String,
    @field:Json(name = "kinds") val kinds: String,
)
