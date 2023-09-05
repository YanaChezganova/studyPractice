package com.example.myapplication1

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CharactersApi {
    @GET("character")
    suspend fun getCharacters(@Query("page") page: Int): ListOfCharacters

    @GET("character/{characterId}")
    suspend fun getCharacterInfo(@Path("characterId") characterId: Int): Character

    @GET("episode/{episodeId}")
    suspend fun getEpisodeInfo(@Path("episodeId") episodeId: List<Int>): List<Episode>
}

private const val BASE_URL = "https://rickandmortyapi.com/api/"
object RetrofitServices {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
    val charactersApi: CharactersApi = retrofit.create(CharactersApi::class.java)}


@JsonClass(generateAdapter = true)
class ListOfCharacters(
    @field:Json(name ="info") val info: Info,
    @field:Json(name ="results") val results: List<Character>

)
@JsonClass(generateAdapter = true)
class Info(
    @field:Json(name ="count") val count: Int,
    @field:Json(name ="pages") val pages: Int
)

@JsonClass(generateAdapter = true)
class Character(
    @field:Json(name ="id") val id: Int,
    @field:Json(name ="name") val name: String,
    @field:Json(name ="status") val status: String,
    @field:Json(name ="species") val species: String,
    @field:Json(name ="gender") val gender: String,
    @field:Json(name ="location") val location: Location,
    @field:Json(name ="image") val image: String,
    @field:Json(name ="origin") val origin: Origin = Origin("", ""),
    @field:Json(name ="episode") val episode: List<String> = emptyList(),
)
@JsonClass(generateAdapter = true)
class Location(
    @field:Json(name ="name") val name: String,
    @field:Json(name ="url") val url: String,)

@JsonClass(generateAdapter = true)
class Origin(
    @field:Json(name ="name") val name: String,
    @field:Json(name ="url") val url: String,)

@JsonClass(generateAdapter = true)
class Episode(
    @field:Json(name ="id") val id: Int,
    @field:Json(name ="name") val name: String,
    @field:Json(name ="air_date") val air_date: String,
    @field:Json(name ="episode") val episodeCode: String,
    @field:Json(name ="characters") val charactersUrl: List<String>,
    @field:Json(name ="url") val url: String,
    @field:Json(name ="created") val created: String,

    )
