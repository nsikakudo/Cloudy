package com.project.cloudy.network

import com.project.cloudy.model.SearchLocationResponse
import com.project.cloudy.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Singleton

@Singleton
interface WeatherApi {
    @GET("forecast.json")
    suspend fun getWeatherForecast(
        @Query("key")api:String = "5441365b93274a0e84a204801222906",
        @Query("q")location:String,
        @Query("days")days:Int
    ): Response<WeatherResponse>


    @GET("search.json")
    suspend fun getCurrentWeather(
        @Query("key")api:String = "5441365b93274a0e84a204801222906",
        @Query("q")location:String,
    ): Response<SearchLocationResponse>

}