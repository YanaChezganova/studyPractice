package com.example.sunnyday.data

import com.example.sunnyday.di.RetrofitService.providesBaseUrl
import com.example.sunnyday.di.RetrofitService.retrofit
import com.example.sunnyday.di.RetrofitService.searchWeather
import com.example.sunnyday.entity.ComplexWeather
import javax.inject.Inject


class NetworkRepository @Inject constructor(){
    suspend fun getWeather(city: String): ComplexWeather {
      // return RetrofitServices.weatherApi.getWeather(city)
       return searchWeather(retrofit(providesBaseUrl())).getWeather(city)
    }

}