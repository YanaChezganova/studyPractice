package com.example.photos.ui.main.support

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface OpenMapApi {
   @GET("/0.1/ru/places/radius?radius=5000&format=geojson&apikey=$myApiKey")
    suspend fun getListOfPlaces(
        @Query("lon") longitude: Double,
        @Query("lat") latitude: Double
        ): ListOfPlaces

    @GET("/0.1/ru/places/xid/{IdOfPlace}?apikey=$myApiKey")
    suspend fun getInformation(@Path("IdOfPlace") IdOfPlace: String):PlaceOfInterest

    private companion object {
        private const val myApiKey = """5ae2e3f221c38a28845f05b6441e03a0aad5619cb4e8e8b012aff9e9"""
    }
}
private const val BASE_URL = "https://api.opentripmap.com/"

object RetrofitServices {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
    val searchPlacesApi: OpenMapApi = retrofit.create(OpenMapApi::class.java)
}
