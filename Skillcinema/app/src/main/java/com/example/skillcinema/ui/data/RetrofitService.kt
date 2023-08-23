package com.example.skillcinema.ui.data

import com.example.skillcinema.ui.models.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
abstract class UsefulActivitiesRepositoryModule : MovieListApi {
    @Binds
    abstract fun bindUsefulActivitiesRepository(
        usefulActivitiesRepository: MovieListApi) : MovieListApi
}
interface MovieListApi {

    @Headers("X-API-KEY: $api_kye")
    @GET("/api/v2.2/films/premieres")
    suspend fun getPremieres(
        @Query("year") year: Int,
        @Query("month") month: String
    ): MovieList

    @Headers("X-API-KEY: $api_kye")
    @GET("/api/v2.2/films/top?type=TOP_100_POPULAR_FILMS")
    suspend fun getPopularList(@Query("page") page: Int): MoviePagedList

    @Headers("X-API-KEY: $api_kye")
    @GET("/api/v2.2/films?order=RATING&type=ALL&ratingFrom=0&ratingTo=10&yearFrom=1000&yearTo=3000")
    suspend fun getRandomListOfFilms(
        @Query("countries") countries: Int,
        @Query("genres") genres: Int,
        @Query("page") page: Int
    ): MovieList

    @Headers("X-API-KEY: $api_kye")
    @GET("/api/v2.2/films/top?type=TOP_250_BEST_FILMS")
    suspend fun getTopList(@Query("page") page: Int): MoviePagedList

    @Headers("X-API-KEY: $api_kye")
    @GET("/api/v2.2/films?&order=RATING&type=TV_SERIES&ratingFrom=0&ratingTo=10&yearFrom=1000&yearTo=3000")
    suspend fun getListOfSerials(
         @Query("page") page: Int
    ): MovieList

    @Headers("X-API-KEY: $api_kye")
    @GET("/api/v2.2/films/{filmId}")
    suspend fun getMovieInformation(@Path("filmId") filmId: Int): Movie

    @Headers("X-API-KEY: $api_kye")
    @GET("/api/v1/staff?&professionKey=ACTOR")
    suspend fun getMovieStaff(@Query("filmId") filmId: Int): List<Person>

    @Headers("X-API-KEY: $api_kye")
    @GET("/api/v2.2/films/{filmId}/similars")
    suspend fun getSimilar(@Path("filmId") filmId: Int): MovieList

    @Headers("X-API-KEY: $api_kye")
    @GET("/api/v2.2/films/{filmId}/images?&page=1")
    suspend fun getPoster(@Path("filmId") filmId: Int,
                          @Query("type") type: String,
                          @Query("page") page: Int): PhotoList

    @Headers("X-API-KEY: $api_kye")
    @GET("/api/v2.2/films/{filmId}/seasons")
    suspend fun getSerialSeasons(@Path("filmId") filmId: Int): SerialSeries

    @Headers("X-API-KEY: $api_kye")
    @GET("/api/v1/staff/{personId}")
    suspend fun getPersonInfo(@Path("personId") personId: Int): Person

    @Headers("X-API-KEY: $api_kye")
    @GET("/api/v2.1/films/search-by-keyword")
    suspend fun getSearchFilmsByWord(@Query("keyword") keyword: String,
                                     @Query("page") page: Int): MoviePagedList

    @Headers("X-API-KEY: $api_kye")
    @GET("/api/v2.2/films")
    suspend fun getSearchFilmsBySettings(@Query("countries") country: Int,
                                         @Query("genres") genre: Int,
                                         @Query("order") order: String,
                                         @Query("type") type: String,
                                         @Query("ratingFrom") ratingFrom: String,
                                         @Query("ratingTo") ratingTo: String,
                                         @Query("yearFrom") yearFrom: String,
                                         @Query("yearTo") yearTo: String,
                                         @Query("page") page: Int
                                         ): MovieList

    private companion object {
        // private const val api_kye = """7fd9941d-1ea2-4b33-8b5f-a84c52cd2094"""
         private const val api_kye = """2cfa223d-a5ba-4319-ad41-1c02b5115497"""
    }
}

private const val BASE_URL = "https://kinopoiskapiunofficial.tech/"

@Module
@InstallIn(SingletonComponent::class)
object RetrofitService {

    @Provides
    fun providesBaseUrl() = "https://kinopoiskapiunofficial.tech/"

    @Provides
    @Singleton
    fun retrofit(BASE_URL: String): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun searchMovie(retrofit: Retrofit): MovieListApi = retrofit.create(MovieListApi::class.java)
}