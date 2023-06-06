package com.project.cloudy.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "weatherResponse")
data class WeatherResponse(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    @SerializedName("current")
    val current: Current,
    @SerializedName("forecast")
    val forecast: Forecast,
    @SerializedName("location")
    val location: Location
)