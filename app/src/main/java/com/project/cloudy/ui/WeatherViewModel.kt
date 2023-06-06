package com.project.cloudy.ui

import android.util.Log
import androidx.lifecycle.*
import androidx.room.withTransaction
import com.project.cloudy.data.WeatherDataBase
import com.project.cloudy.repository.WeatherRepository
import com.project.cloudy.utils.Resource
import com.project.cloudy.model.SavedWeather
import com.project.cloudy.model.SearchLocationResponse
import com.project.cloudy.model.WeatherResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val db: WeatherDataBase
) : ViewModel() {
    val weatherForecast = MutableLiveData<Resource<WeatherResponse>>()
    val searchLocationResult = MutableLiveData<Resource<SearchLocationResponse>>()
    val searchLocationWeatherResult = MutableLiveData<Resource<WeatherResponse>>()

    sealed class Events {
        object Successful : Events()
        object Failure : Events()
    }

    private val _savingEvent = Channel<Events>()
    val savingEvents = _savingEvent.receiveAsFlow()

    val frmDab = MutableLiveData<WeatherResponse>()
    val situFrmDab = MutableLiveData<Resource<WeatherResponse>>()
    val savedWeatherResult = MutableLiveData<List<SavedWeather>>()
    val onlineSavedWeatherResultTest = MutableLiveData<Resource<List<SavedWeather>>>()
    val offLineSavedWeatherResultTest = MutableLiveData<Resource<List<SavedWeather>>>()

    fun reportDbSitu() {
        viewModelScope.launch {
            val isDbEmpty = db.weatherDao().getSize() < 1
            when {
                isDbEmpty -> {
                    situFrmDab.value = Resource.Failure("Db is empty")
                }
                else -> {
                    situFrmDab.value = Resource.Successful(repository.getAllWeatherFromDb())
                }
            }
        }
    }

    fun updateWeather(location: String) {
        weatherForecast.value = Resource.Loading()
        viewModelScope.launch {
            when (val forecast = repository.getWeatherForecast(location = location, 7)) {
                is Resource.Successful -> {
                    forecast.data?.let {
                        weatherForecast.value = Resource.Successful(it)
                        db.withTransaction {
                            deleteAllWeather()
                            Log.d("dbtransaction", "deleting weather")
                            insertAllWeather(it)
                            Log.d("dbtransaction", "inserting weather")
                        }
                    }
                }
                is Resource.Failure -> {
                    forecast.msg?.let {
                        weatherForecast.value = Resource.Failure(it)
                    }
                }
                is Resource.Empty -> {
                    weatherForecast.value = Resource.Empty()
                }
                else -> Unit
            }
        }
    }

    fun updateSearchedLocation(name: String) {
        searchLocationResult.value = Resource.Loading()
        viewModelScope.launch {
            when (val searchLocation = repository.getSearchedLocation(name)) {
                is Resource.Successful -> {
                    if (searchLocation.data?.isEmpty() == true || searchLocation.data?.size!! <= 0) {
                        searchLocationResult.value = Resource.Empty()
                    } else {
                        searchLocationResult.value = Resource.Successful(searchLocation.data)
                    }
                }
                is Resource.Failure -> {
                    searchLocation.msg?.let {
                        searchLocationResult.value = Resource.Failure(it)
                    }
                }
                is Resource.Empty -> {
                    searchLocationResult.value = Resource.Empty()
                }
                else -> {}
            }
        }
    }

    fun updateWeatherSearchedLocation(location: String) {
        viewModelScope.launch {
            searchLocationWeatherResult.value = Resource.Loading()
            when (val forecast = repository.getWeatherForSearchedLocation(location = location, 7)) {
                is Resource.Successful -> {
                    forecast.data?.let {
                        searchLocationWeatherResult.value = Resource.Successful(it)
                    }
                }
                is Resource.Failure -> {
                    forecast.msg?.let {
                        searchLocationWeatherResult.value = Resource.Failure(it)
                    }
                }
                is Resource.Empty -> {
                    searchLocationWeatherResult.value = Resource.Empty()
                }
                else -> Unit
            }
        }
    }

    private fun getAllWeatherFromDb() {
        viewModelScope.launch {
            repository.getAllWeatherFromDb()
        }
    }

    private fun insertAllWeather(weatherResponse: WeatherResponse) {
        viewModelScope.launch {
            repository.insertAllWeather(weatherResponse)
        }
    }

    private fun deleteAllWeather() {
        viewModelScope.launch {
            repository.deleteAllWeather()
        }
    }


    private suspend fun getAllSavedWeather(): Flow<List<SavedWeather>> =
        repository.getAllSavedWeather()

    fun insertSavedWeather(savedWeather: SavedWeather) {
        viewModelScope.launch {
            repository.insertAllSavedWeather(savedWeather)
        }
    }

    fun deleteSavedWeather(savedWeather: SavedWeather) {
        viewModelScope.launch {
            repository.deleteSavedWeather(savedWeather)
        }
    }

    private fun deleteAllSavedWeather() {
        viewModelScope.launch {
            repository.deleteAllSavedWeather()
        }
    }

    private val onlineWeatherList = mutableListOf<SavedWeather>()
    fun doOnlineSavedOperation() {
        onlineSavedWeatherResultTest.value = Resource.Loading()

        viewModelScope.launch {
            val isDbEmpty = db.savedWeatherDao().getSavedSize() < 1
            when {
                isDbEmpty -> {
                    onlineSavedWeatherResultTest.value = Resource.Failure("List is empty")
                }
                else -> {
                    db.withTransaction {
                        onlineWeatherList.clear()
                        repository.getAllSavedWeather().first().forEach { savedWeather ->
                            when (val getWeather =
                                repository.getWeatherForSearchedLocation(
                                    savedWeather.location!!.name,
                                    4
                                )) {
                                is Resource.Successful -> {
                                    val location = getWeather.data?.location
                                    val current = getWeather.data?.current
                                    val weather = savedWeather.copy(
                                        location = location,
                                        current = current
                                    )
                                    onlineWeatherList.add(weather)
                                    db.savedWeatherDao().updateSavedWeather(weather)
                                }
                                is Resource.Failure -> {
                                    Log.d("savedProcess", "${getWeather.msg}")
                                }
                                else -> {}
                            }

                        }
                        Log.d(
                            "savedProcess",
                            " online size = ${onlineWeatherList.size}----->>$onlineWeatherList"
                        )
                    }
                    onlineSavedWeatherResultTest.value = Resource.Successful(onlineWeatherList)
                }
            }
        }

    }

    fun reportSavedDbSitu() {
        offLineSavedWeatherResultTest.value = Resource.Loading()
        viewModelScope.launch {
            val isSavedEmpty = db.savedWeatherDao().getSavedSize() < 1
            when {
                isSavedEmpty -> {
                    offLineSavedWeatherResultTest.value =
                        Resource.Failure("List is empty")
                    Log.d("savedProcess", "less than 0")
                }
                else -> {
                    repository.getAllSavedWeather().first().toList().let {
                        offLineSavedWeatherResultTest.value = Resource.Successful(it)
                    }
                }
            }
        }
    }

    fun saveToDb(savedWeather: SavedWeather) {
        viewModelScope.launch {
            val savedSize = db.savedWeatherDao().getSavedSize() < 5
            when {
                savedSize -> {
                    insertSavedWeather(savedWeather)
                    _savingEvent.send(Events.Successful)
                }
                else -> {
                    _savingEvent.send(Events.Failure)
                    Log.d("size", "failed to save to db")
                }
            }
        }
    }
}