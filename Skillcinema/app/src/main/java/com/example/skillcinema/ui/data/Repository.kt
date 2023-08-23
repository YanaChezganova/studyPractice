package com.example.skillcinema.ui.data

import android.content.Context
import android.util.Log
import com.example.skillcinema.ui.data.RetrofitService.providesBaseUrl
import com.example.skillcinema.ui.data.RetrofitService.retrofit
import com.example.skillcinema.ui.data.RetrofitService.searchMovie
import com.example.skillcinema.ui.models.*
import javax.inject.Inject


private const val PREFERENCE_NAME = "skillCinema_preference"

class Repository @Inject constructor() {
    suspend fun getPremieres(year: Int, month: String): MovieList {
        return searchMovie(retrofit(providesBaseUrl())).getPremieres(year, month)
    }
    suspend fun getPopular(page: Int): MoviePagedList {
        return searchMovie(retrofit(providesBaseUrl())).getPopularList(page)
    }
    suspend fun getRandomListOfFilms(countries: Int, genres: Int, page: Int): MovieList {
        return searchMovie(retrofit(providesBaseUrl())).getRandomListOfFilms(countries, genres, page)
    }
    suspend fun getTopList(page: Int): MoviePagedList {
        return searchMovie(retrofit(providesBaseUrl())).getTopList(page)
    }
    suspend fun getSerials(page: Int): MovieList {
        return searchMovie(retrofit(providesBaseUrl())).getListOfSerials(page)
    }
    suspend fun getMovieInformation(movieId: Int): Movie {
        return searchMovie(retrofit(providesBaseUrl())).getMovieInformation(movieId)
    }
    suspend fun getMovieStaff(movieId: Int): List<Person> {
        return searchMovie(retrofit(providesBaseUrl())).getMovieStaff(movieId)
    }
    suspend fun getSimilarFilms(movieId: Int): MovieList {
        return searchMovie(retrofit(providesBaseUrl())).getSimilar(movieId)
    }
    suspend fun getPoster(movieId: Int, type: String, page: Int): PhotoList {
        return searchMovie(retrofit(providesBaseUrl())).getPoster(movieId, type, page)
    }
    suspend fun getSerialSeries(movieId: Int): SerialSeries {
        return searchMovie(retrofit(providesBaseUrl())).getSerialSeasons(movieId)
    }
    suspend fun getPersonInfo(personId: Int): Person {
        return searchMovie(retrofit(providesBaseUrl())).getPersonInfo(personId)
    }
    suspend fun getSearchResult(keyword: String, page: Int): MoviePagedList {
        return searchMovie(retrofit(providesBaseUrl())).getSearchFilmsByWord(keyword, page)
    }
    fun transformCountries(): List<Country> {
        val string = Transform().countryString
        val listOfCountries = Transform().adapterPostCountries().fromJson(string)
        return listOfCountries?.countries!!
    }
    fun transformGenres(): List<Genre> {
        val string = Transform().genreString
        val listOfCountries = Transform().adapterPostGenres().fromJson(string)
        return listOfCountries?.genres!!
    }
    fun loadFromSharedPreference(context: Context, key: String): String {
        val value: String
        val prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)!!
        value = prefs.getString(key, "...").toString()
        return value
    }
    fun loadToSharedPreference(context: Context, key: String, value: String?) {
        val prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)!!
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }
    fun clearSharedPreference(context: Context, key: String) {  //не все, а по ключам. тк AppIntro надо оставить KEY
        val prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)!!
        val editor = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit()

        try {
            editor.remove(key)
            editor.apply()
        } catch (e: Exception) {
            Log.d("onCleared", "not cleared shared preference")
        } finally {
            Log.d("finally SVM", "preference ${prefs.all}")
        }
    }
    suspend fun getSearchFilmsBySettings(
        country: Int, genre: Int,
        order: String, type: String,
        ratingFrom: String,
        ratingTo: String,
        yearFrom: String,
        yearTo: String,
        page: Int
    ): MovieList {
        return searchMovie(retrofit(providesBaseUrl())).getSearchFilmsBySettings(
            country, genre,
            order, type,
            ratingFrom, ratingTo,
            yearFrom, yearTo, page
        )
    }

}
