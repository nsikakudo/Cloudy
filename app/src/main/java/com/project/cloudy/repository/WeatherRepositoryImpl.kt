package com.project.cloudy.repository

import android.util.Log
import com.project.cloudy.data.WeatherDataBase
import com.project.cloudy.network.WeatherApi
import com.project.cloudy.utils.Resource
import com.project.cloudy.model.SavedWeather
import com.project.cloudy.model.SearchLocationResponse
import com.project.cloudy.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(val api: WeatherApi,val db: WeatherDataBase) : WeatherRepository {
    override suspend fun getWeatherForecast(
        location: String,
        days: Int
    ): Resource<WeatherResponse> {
        return try {
            val result = api.getWeatherForecast(location = location, days = days)
            val response = result.body()!!
            Log.d("RESPONSE-S", "$response")
            Log.d("LOCATION", "${response.location}")
            Resource.Successful(response)
        } catch (e: Exception) {
            Log.d("RESPONSE-F", "$e")
            Resource.Failure("AN EXCEPTION OCCURRED <---> ${e.message}")
        }

    }

    override suspend fun getSearchedLocation(name:String): Resource<SearchLocationResponse> {
        return try {
            val result = api.getCurrentWeather(location = name)
            val response = result.body()!!
            Log.d("RESPONSE-S-search", "$response")
            Log.d("LOCATION", "${response}")
            Resource.Successful(response)
        }catch (e:Exception){
            Log.d("RESPONSE-F-search", "$e")
            Resource.Failure("$e")
        }
    }

    override suspend fun getWeatherForSearchedLocation(
        location: String,
        days: Int
    ): Resource<WeatherResponse> {
        return try {
            val result = api.getWeatherForecast(location = location, days = days)
            val response = result.body()!!
            Log.d("RESPONSE-S-w", "$response")
            Log.d("LOCATION", "${response.location.name}")
            Resource.Successful(response)
        } catch (e: Exception) {
            Log.d("RESPONSE-F-W", "$e")
            Resource.Failure("AN EXCEPTION OCCURRED <---> ${e.message}")
        }
    }

    //REGULAR FORECAST
    override suspend fun getAllWeatherFromDb(): WeatherResponse = db.weatherDao().getWeatherResponse()
    override suspend fun insertAllWeather(weatherResponse: WeatherResponse) = db.weatherDao().insertWeatherResponse(weatherResponse)
    override suspend fun deleteAllWeather() = db.weatherDao().deleteAll()


    //SAVED WEATHER
    override suspend fun getAllSavedWeather(): Flow<List<SavedWeather>> = db.savedWeatherDao().getAllSavedWeather()
    override suspend fun insertAllSavedWeather(savedWeather: SavedWeather) = db.savedWeatherDao().insertWeather(savedWeather)
    override suspend fun deleteSavedWeather(savedWeather: SavedWeather) = db.savedWeatherDao().deleteSavedWeather(savedWeather)
    override suspend fun deleteAllSavedWeather() =db.savedWeatherDao().deleteAllSavedWeather()
}

