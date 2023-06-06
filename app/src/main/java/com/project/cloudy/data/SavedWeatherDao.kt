package com.project.cloudy.data

import androidx.room.*
import com.project.cloudy.model.Location
import com.project.cloudy.model.SavedWeather
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedWeatherDao {

    @androidx.room.Query("SELECT* FROM saved_weather")
    fun getAllSavedWeather():Flow<List<SavedWeather>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(savedWeather: SavedWeather)

    @Delete
    suspend fun deleteSavedWeather(savedWeather: SavedWeather)

    @androidx.room.Query("SELECT COUNT() FROM saved_weather")
    suspend fun getSavedSize():Int

    @androidx.room.Query("DELETE FROM saved_weather")
    suspend fun deleteAllSavedWeather()

    @androidx.room.Query("SELECT COUNT() FROM saved_weather WHERE location =:name" )
    suspend fun checkIfExist(name: Location):Int

    @Update
    suspend fun updateSavedWeather(savedWeather: SavedWeather)
}