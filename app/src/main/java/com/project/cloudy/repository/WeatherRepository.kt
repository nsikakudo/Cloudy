package com.project.cloudy.repository

import com.project.cloudy.utils.Resource
import com.project.cloudy.model.SavedWeather
import com.project.cloudy.model.SearchLocationResponse
import com.project.cloudy.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    suspend fun getWeatherForecast(location:String,days:Int):Resource<WeatherResponse>
    suspend fun getSearchedLocation(name:String):Resource<SearchLocationResponse>
    suspend fun getWeatherForSearchedLocation(location:String,days: Int):Resource<WeatherResponse>

    suspend fun getAllWeatherFromDb(): WeatherResponse
    suspend fun insertAllWeather(weatherResponse: WeatherResponse)
    suspend fun deleteAllWeather()


    suspend fun getAllSavedWeather(): Flow<List<SavedWeather>>
    suspend fun insertAllSavedWeather(savedWeather: SavedWeather)
    suspend fun deleteSavedWeather(savedWeather: SavedWeather)
    suspend fun deleteAllSavedWeather()
}