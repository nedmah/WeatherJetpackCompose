package com.example.gorbunovmobdev

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.gorbunovmobdev.weatherRetrofit.WeatherApiService
import com.example.gorbunovmobdev.weatherRetrofit.WeatherResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Properties

class WeatherFetching {

    fun fetchWeatherData(city: String, context: Context, callback: (WeatherResponse?) -> Unit) {
        val service = WeatherApiService.create()
        val apiKey = openProperties(context)



        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.getWeatherByCityName(city, apiKey)
                withContext(Dispatchers.Main) {
                    callback(response)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        GreetingsActivity(),
                        "Ошибка получения данных, попробуйте ввести другой город",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }


    fun fetchWeatherDataByLocation(lat: Double, long: Double, context: Context, callback: (WeatherResponse?) -> Unit) {
        val service = WeatherApiService.create()
        val apiKey = openProperties(context)



        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.getWeatherByCoordinates(lat,long, apiKey)
                withContext(Dispatchers.Main) {
                    callback(response)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Ошибка получения данных, попробуйте ввести другой город",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }

    private fun openProperties(context: Context) : String{
        val properties = Properties()
        var apiKey = ""
        try {
            properties.load(context.assets.open("api_key.properties"))
            apiKey = properties.getProperty("API_KEY")
            // Use the API key to fetch weather data
        } catch (e: IOException) {
            // Handle the error
        }
        return apiKey
    }



}