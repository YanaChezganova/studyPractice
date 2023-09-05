package com.example.sunnyday.di

import com.example.sunnyday.entity.ComplexWeather
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule : WeatherApi {
    @Binds
    abstract fun bindNetworkRepository(
        NetworkModuleRepository: WeatherApi) : WeatherApi
}

interface WeatherApi {


    @GET("/current?access_key=$weather_stack_api_key")
    suspend fun getWeather(
        @Query("query") city: String
    ): ComplexWeather



    private companion object {
        private const val weather_stack_api_key = """124b8e98f20e69895fa0d58952b4d59d"""
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RetrofitService {

    @Provides
    fun providesBaseUrl() = "http://api.weatherstack.com/"

    @Provides
    @Singleton
    fun retrofit(providesBaseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(providesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun searchWeather( retrofit: Retrofit): WeatherApi = retrofit.create(WeatherApi::class.java)
}
