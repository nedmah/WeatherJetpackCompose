package com.example.gorbunovmobdev

import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.gorbunovmobdev.ui.theme.GorbunovMobDevTheme
import com.example.gorbunovmobdev.ui.theme.brown
import com.example.gorbunovmobdev.ui.theme.gold
import com.example.gorbunovmobdev.weatherRetrofit.LocationProvider
import com.example.gorbunovmobdev.weatherRetrofit.WeatherApiService
import com.example.gorbunovmobdev.weatherRetrofit.WeatherResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.Properties

class GreetingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GorbunovMobDevTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun Greeting() {
    var city by remember { mutableStateOf("") }
    var weatherData by remember { mutableStateOf<WeatherResponse?>(null) }
    val context = LocalContext.current

    val updateWeatherData: (WeatherResponse?) -> Unit = { response ->
        weatherData = response
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Spacer(modifier = Modifier.height(120.dp))

        Text(
            text = "Погода в вашем городе",
            modifier = Modifier,
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = gold)

        )

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            value = city,
            onValueChange = {city = it.take(18)},
            label = { Text(text = "Введите город") },
            modifier = Modifier.border(
                shape = RoundedCornerShape(28.dp),
                width = 2.dp,
                color = gold
            ),
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black,
            )
        )

        Spacer(modifier = Modifier.height(20.dp))


        Row {



            Button(
                onClick = {

                    val location = LocationProvider(context)
                    location.getCurrentLocation()
                    val lat = location.latitude
                    val long = location.longitude
                    fetchWeatherDataByLocation(lat,long,context,updateWeatherData)

                },
                modifier = Modifier
            ) {
                Image(
                    painter = painterResource(id = R.drawable.geo),
                    contentDescription = null // замените null на подходящее описание
                )
            }

        Button(
            onClick = {

                if (city.isNotEmpty()) fetchWeatherData(city,context, updateWeatherData)
                else{
                    val location = LocationProvider(context)
                    location.getCurrentLocation()
                    val lat = location.latitude
                    val long = location.longitude
                    fetchWeatherDataByLocation(lat,long,context,updateWeatherData)
                }


            },
            modifier = Modifier
        ) {
            Text(text = "Узнать")
        }

    }

        Spacer(modifier = Modifier.height(20.dp))

        weatherData?.let { data ->
            val temp = data.main.temp - 273.15
            val feelsLike = data.main.feels_like - 273.15
            Text(
                text = "Температура: ${temp.toInt()}°C\n" +
                        "Ощущается как: ${feelsLike.toInt()}°C\n" +
                        "Влажность: ${data.main.humidity}%\n" +
                        "Погода: ${data.weather.firstOrNull()?.main ?: ""}",
                modifier = Modifier,
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, color = brown)
            )


            val iconUrl = data.weather.firstOrNull()?.getIconUrl()
            iconUrl?.let {
                Image(
                    painter = rememberImagePainter(data = iconUrl),
                    contentDescription = null,
                    modifier = Modifier.size(90.dp)
                )
            }

        }





    }
}


private fun fetchWeatherData(city: String, context: Context, callback: (WeatherResponse?) -> Unit) {
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


private fun fetchWeatherDataByLocation(lat: Double, long: Double, context: Context, callback: (WeatherResponse?) -> Unit) {
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
