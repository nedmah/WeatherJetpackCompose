package com.example.gorbunovmobdev.weatherRetrofit

data class WeatherResponse(
    val main: Main,
    val weather: List<WeatherDetail>,
    val name: String
)

data class Main(
    val temp: Float,
    val feels_like: Float,
    val humidity: Float
)

data class WeatherDetail(
    val id: Int,
    val main: String,
    val icon: String
)
{
    fun getIconUrl(): String {
        return "https://openweathermap.org/img/wn/$icon.png"
    }
}