package com.project.cloudy.di

import android.content.Context
import androidx.room.Room
import com.project.cloudy.data.SavedWeatherDao
import com.project.cloudy.data.WeatherDao
import com.project.cloudy.data.WeatherDataBase
import com.project.cloudy.network.WeatherApi
import com.project.cloudy.repository.WeatherRepository
import com.project.cloudy.repository.WeatherRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun getDao(db: WeatherDataBase): WeatherDao = db.weatherDao()
    fun getSavedDao(db: WeatherDataBase): SavedWeatherDao = db.savedWeatherDao()

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext appContext: Context) =
        Room.databaseBuilder(
            appContext,
            WeatherDataBase::class.java,
            "WEATHER_DATABASE"
        ).build()

    @Singleton
    @Provides
    fun getOkHttp(): OkHttpClient {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BASIC
        return OkHttpClient.Builder().addInterceptor(logger).build()
    }

    @Singleton
    @Provides
    fun getMainRepImpl(api: WeatherApi,db: WeatherDataBase):WeatherRepository = WeatherRepositoryImpl(api,db)



    @Singleton
    @Provides
    fun getRetrofit(http: OkHttpClient): WeatherApi =
        Retrofit.Builder()
            .client(http)
            .baseUrl("http://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)

}