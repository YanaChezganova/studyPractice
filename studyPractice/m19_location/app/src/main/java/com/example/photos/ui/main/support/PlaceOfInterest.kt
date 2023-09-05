package com.example.photos.ui.main.support

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaceOfInterest(
    @field:Json(name ="xid") val xid: String,
    @field:Json(name ="name") val nameOfPlace: String = "нет данных",
    @field:Json(name ="address") val address: Address,
    @field:Json(name ="rate") val rate: String = "нет данных",
    @field:Json(name ="wikidata") val wikidata: String = "нет данных",
    @field:Json(name ="kinds") val kinds: String,
    @field:Json(name ="url") val urlInformation: String = "нет данных",
    @field:Json(name ="sources") val sources: Sources,
    @field:Json(name ="otm") val onOpenTripMapUrl: String,
    @field:Json(name ="info") val infoAboutPlace: InfoAboutPlace = InfoAboutPlace("","",
        "Идет сбор данных. Присоединяйтесь! https://api.opentripmap.com \n","",""),
    @field:Json(name ="wikipedia") val wikipediaUrl: String = "нет данных",
    @field:Json(name ="image") val imageUrl: String = "https://upload.wikimedia.org/wikipedia/ru/thumb/a/ac/No_image_available.svg/1200px-No_image_available.svg.png",
    @field:Json(name ="preview") val preview: Preview = Preview("https://upload.wikimedia.org/wikipedia/ru/thumb/a/ac/No_image_available.svg/1200px-No_image_available.svg.png",0,0),
    @field:Json(name ="wikipedia_extracts") val textAboutPlace: TextAboutPlace = TextAboutPlace("Идет сбор данных",
        "Идет сбор данных.  Присоединяйтесь! https://api.opentripmap.com \n "),
    @field:Json(name ="point") val pointLocation: PointLocation
)
@JsonClass(generateAdapter = true)
data class Address(
    @field:Json(name ="road") val road: String = "нет данных",
    @field:Json(name ="house") val house: String = "нет данных",
    @field:Json(name ="state") val state: String = "нет данных",
    @field:Json(name ="suburb") val suburb: String = "нет данных",
    @field:Json(name ="country") val country: String = "нет данных",
    @field:Json(name ="postcode") val postcode: String = "нет данных",
    @field:Json(name ="country_code") val country_code: String = "нет данных",
    @field:Json(name ="house_number") val house_number: String = "нет данных",
    @field:Json(name ="state_district") val state_district: String = "нет данных"
    )
@JsonClass(generateAdapter = true)
data class Sources(
    @field:Json(name ="geometry") val geometry: String = "нет данных",
    @field:Json(name ="attributes") val attributes: List<String>,
    )
@JsonClass(generateAdapter = true)
data class Preview(
    @field:Json(name ="source") val sourceUrl: String = "https://upload.wikimedia.org/wikipedia/ru/thumb/a/ac/No_image_available.svg/1200px-No_image_available.svg.png",
    @field:Json(name ="height") val height: Int = 0,
    @field:Json(name ="width") val width: Int =0
    )
@JsonClass(generateAdapter = true)
data class TextAboutPlace(
    @field:Json(name ="title") val title: String = "нет данных",
    @field:Json(name ="text") val text: String = "нет данных"
    )
@JsonClass(generateAdapter = true)
data class PointLocation(
    @field:Json(name ="lon") val longitude: Double,
    @field:Json(name ="lat") val latitude: Double
    )
@JsonClass(generateAdapter = true)
data class InfoAboutPlace(
    @field:Json(name ="src") val resource: String,
    @field:Json(name ="url") val urlResource: String,
    @field:Json(name ="descr") val description: String,
    @field:Json(name ="image") val imageName: String,
    @field:Json(name ="src_id") val sourceId: String
)
