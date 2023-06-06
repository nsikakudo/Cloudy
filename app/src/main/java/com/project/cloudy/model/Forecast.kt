package com.project.cloudy.model


import com.google.gson.annotations.SerializedName
import com.project.cloudy.model.Forecastday

data class Forecast(
    @SerializedName("forecastday")
    val forecastday: List<Forecastday>
)