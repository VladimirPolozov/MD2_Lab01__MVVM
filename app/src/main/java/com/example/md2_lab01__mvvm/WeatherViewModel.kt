package com.example.md2_lab01__mvvm

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

class WeatherViewModel(private val service: RetrofitServices,
                       private val resources: Resources
) : ViewModel() {
    private val _weatherData = MutableLiveData<List<WeatherList>>()
    val weatherData: LiveData<List<WeatherList>> get() = _weatherData

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> = _toastMessage

    fun fetchWeather(city: String) {
        val apiKey = ""
        val call = service.getWeatherListByCity(city, apiKey)

        call.enqueue(object : Callback<WeatherWrapper> {
            override fun onResponse(call: Call<WeatherWrapper>, response: Response<WeatherWrapper>) {
                when {
                    response.code() == 404 -> _toastMessage.value = "There is no such city found"
                    response.body() == null ->
                        _toastMessage.value = "Format data error"
                }
                if (response.isSuccessful) {
                    response.body()?.list?.let {
                        _weatherData.value = it
                    }
                }
            }

            override fun onFailure(call: Call<WeatherWrapper>, t: Throwable) {
                _toastMessage.value = "Network error: ${t.localizedMessage}"
            }
        })
    }
}

interface RetrofitServices {
    @GET("data/2.5/forecast")
    fun getWeatherListByCity(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String
    ): Call<WeatherWrapper>
}