package com.project.cloudy.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.project.cloudy.model.SavedWeather
import com.project.cloudy.model.WeatherResponse

@Database(entities = [WeatherResponse::class, SavedWeather::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WeatherDataBase: RoomDatabase(){
    abstract fun weatherDao():WeatherDao
    abstract fun savedWeatherDao():SavedWeatherDao
}