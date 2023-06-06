package com.project.cloudy.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.project.cloudy.model.WeatherResponse

@Dao
interface WeatherDao {
    @androidx.room.Query("SELECT* FROM weatherResponse")
    suspend fun getWeatherResponse(): WeatherResponse

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherResponse(weatherResponse: WeatherResponse)

    @androidx.room.Query("DELETE FROM weatherResponse")
    suspend fun deleteAll()

    @androidx.room.Query("SELECT COUNT() FROM weatherResponse")
    suspend fun getSize():Int
}