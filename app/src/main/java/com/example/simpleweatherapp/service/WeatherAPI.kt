package com.example.simpleweatherapp.service

import com.example.simpleweatherapp.model.Model
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {

//   http://api.openweathermap.org/data/2.5/weather?q=baku&APPID=c100c5e2951388b7b6d74c520a772109

    @GET("data/2.5/weather?&units=metric&APPID=c100c5e2951388b7b6d74c520a772109")
    fun getData(
        @Query("q")
        cityName: String
    ): Single<Model>
}