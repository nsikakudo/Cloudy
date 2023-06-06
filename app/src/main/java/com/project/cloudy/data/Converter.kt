package com.project.cloudy.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.project.cloudy.model.Current
import com.project.cloudy.model.Forecast
import com.project.cloudy.model.Location

class Converters {
    @TypeConverter
    fun fromForecast(forecast: Forecast): String {
        return Gson().toJson(forecast)
    }

    @TypeConverter
    fun toForecast(value: String): Forecast {
        return Gson().fromJson(value, Forecast::class.java)
    }

    @TypeConverter
    fun fromLocation(location: Location): String {
        return Gson().toJson(location)
    }

    @TypeConverter
    fun toLocation(value: String): Location {
        return Gson().fromJson(value, Location::class.java)
    }

    @TypeConverter
    fun fromCurrent(current: Current): String {
        return Gson().toJson(current)
    }

    @TypeConverter
    fun toCurrent(value: String): Current {
        return Gson().fromJson(value, Current::class.java)
    }

}