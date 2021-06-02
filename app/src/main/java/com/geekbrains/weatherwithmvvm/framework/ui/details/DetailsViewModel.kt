package com.geekbrains.weatherwithmvvm.framework.ui.details

import androidx.lifecycle.*
import com.geekbrains.weatherwithmvvm.model.AppState
import com.geekbrains.weatherwithmvvm.model.Repository
import com.geekbrains.weatherwithmvvm.model.RepositoryImpl
import com.geekbrains.weatherwithmvvm.model.database.Database
import com.geekbrains.weatherwithmvvm.model.entities.Weather
import com.geekbrains.weatherwithmvvm.model.rest.rest_entities.WeatherDTO
import com.geekbrains.weatherwithmvvm.model.rest.rest_interaction.BackendRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IllegalStateException

class DetailsViewModel : ViewModel(), LifecycleObserver, CoroutineScope by MainScope() {
    private val repository: Repository = RepositoryImpl()
    val liveDataToObserve: MutableLiveData<AppState> = MutableLiveData()

    fun loadData(cityName: String, lat: Double, lng: Double) {
        liveDataToObserve.value = AppState.Loading
        launch(Dispatchers.IO) {
            val data = repository.getWeatherFromServer(lat, lng).apply {
                city.city = cityName
            }
            liveDataToObserve.postValue(AppState.Success(listOf(data)))
            repository.saveEntity(data)
        }
    }

    fun loadDataAsync(lat: Double, lng: Double) {
        val callback = object : Callback<WeatherDTO> {
            override fun onResponse(call: Call<WeatherDTO>, response: Response<WeatherDTO>) {
                if(response.isSuccessful) {
                    val model = Weather(
                        temperature = response.body()?.factWeather?.temp,
                        feelsLike = response.body()?.factWeather?.feelsLike,
                        condition = response.body()?.factWeather?.condition
                    )
                    launch(Dispatchers.IO) { repository.saveEntity(model) }
                    liveDataToObserve.postValue(
                        AppState.Success(listOf(model))
                    )
                } else {
                    if(response.code() == 401) {
                        //мы не авторизованы - открываем экран логинки
                    }
                    liveDataToObserve.postValue(AppState.Error(IllegalStateException()))
                }
            }

            override fun onFailure(call: Call<WeatherDTO>, t: Throwable) {
                t.printStackTrace()
            }
        }

        repository.getWeatherFromServerAsync(lat, lng, callback)
    }
}