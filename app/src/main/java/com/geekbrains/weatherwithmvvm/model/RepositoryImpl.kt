package com.geekbrains.weatherwithmvvm.model

import com.geekbrains.weatherwithmvvm.model.database.Database
import com.geekbrains.weatherwithmvvm.model.database.HistoryEntity
import com.geekbrains.weatherwithmvvm.model.entities.City
import com.geekbrains.weatherwithmvvm.model.entities.Weather
import com.geekbrains.weatherwithmvvm.model.entities.getRussianCities
import com.geekbrains.weatherwithmvvm.model.entities.getWorldCities
import com.geekbrains.weatherwithmvvm.model.rest.rest_entities.WeatherDTO
import com.geekbrains.weatherwithmvvm.model.rest.rest_interaction.BackendRepo
import retrofit2.Callback

class RepositoryImpl : Repository {
    override fun getWeatherFromServer(lat: Double, lng: Double): Weather {
        val dto = BackendRepo.api.getWeather(lat, lng).execute().body()
        return Weather(
            temperature = dto?.factWeather?.temp,
            feelsLike = dto?.factWeather?.feelsLike,
            condition = dto?.factWeather?.condition
        )
    }

    override fun getWeatherFromServerAsync(lat: Double, lng: Double, callback: Callback<WeatherDTO>) {
        BackendRepo.api.getWeather(lat, lng).enqueue(callback)
    }

    override fun getWeatherFromLocalStorageRus() = getRussianCities()
    override fun getWeatherFromLocalStorageWorld() = getWorldCities()

    override fun getAllHistory(): List<Weather> =
        convertHistoryEntityToWeather(Database.db.historyDao().all())


    override fun saveEntity(weather: Weather) {
        Database.db.historyDao().insert(convertWeatherToEntity(weather))
    }

    private fun convertHistoryEntityToWeather(entityList: List<HistoryEntity>): List<Weather> =
        entityList.map {
            Weather(City(it.city, 0.0, 0.0), it.temperature, 0, it.condition)
        }


    private fun convertWeatherToEntity(weather: Weather): HistoryEntity =
        HistoryEntity(0, weather.city.city,
            weather.temperature ?: 0,
            weather.condition ?: ""
        )
}