package com.example.gorbunovmobdev

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.background
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import coil.Coil
import coil.compose.rememberImagePainter
import coil.util.CoilUtils
import com.bumptech.glide.Glide
import com.example.gorbunovmobdev.ui.theme.brown
import com.example.gorbunovmobdev.ui.theme.gold
import com.example.gorbunovmobdev.ui.theme.jazzblue
import com.example.gorbunovmobdev.ui.theme.jazzblue1
import com.example.gorbunovmobdev.weatherRetrofit.LocationProvider
import com.example.gorbunovmobdev.weatherRetrofit.WeatherResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class WeatherWidget : GlanceAppWidget() {


    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                val weatherFetching = WeatherFetching()
                var weatherData by remember { mutableStateOf<WeatherResponse?>(null) }
                val updateWeatherData: (WeatherResponse?) -> Unit = { response ->
                    weatherData = response
                }
                val location = LocationProvider(context)
                location.getCurrentLocation()
                val lat = location.latitude
                val long = location.longitude

                weatherFetching.fetchWeatherDataByLocation(lat,long, context, updateWeatherData)

                val sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
                val isLoggedIn = sharedPreferences.getBoolean("loggedIn", false)

                when(isLoggedIn){
                    true -> WeatherContent(context, weatherData)
                    false -> weatherAuthorize()
                }
            }
        }
    }

}

class WeatherWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WeatherWidget()
    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        WidgetUpdateScheduler.scheduleHourlyUpdate(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        WidgetUpdateScheduler.cancelUpdate(context)
    }
}


@Composable
fun WeatherContent(context: Context, weatherData: WeatherResponse?) {

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    val currentTime = remember { mutableStateOf(LocalTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(60000) // Обновление времени каждую минуту
            currentTime.value = LocalTime.now()
        }
    }

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color.Transparent))
            .cornerRadius(25.dp),
        verticalAlignment = Alignment.Vertical.CenterVertically,
        horizontalAlignment = Alignment.Horizontal.Start,
    ) {

    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(ColorProvider(Color.Transparent)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.End
    ) {

        Text(
            modifier = GlanceModifier,
            text = "Обновлено в ${currentTime.value.format(DateTimeFormatter.ofPattern("HH:mm"))}",
            style = androidx.glance.text.TextStyle(
                fontSize = 14.sp,
                fontWeight = androidx.glance.text.FontWeight.Bold,
                color = ColorProvider(jazzblue1)
            )
        )
        androidx.glance.Image(
            modifier = GlanceModifier
                .width(30.dp)
                .height(30.dp)
                .clickable(actionRunCallback<RefreshWeatherAction>())
            ,
            provider = ImageProvider(R.drawable.refresh),
            contentDescription = "weather icon"
        )
    }

    Column(
        modifier = GlanceModifier
//            .fillMaxSize()
            .background(ColorProvider(jazzblue))
            .cornerRadius(25.dp),
        verticalAlignment = Alignment.Vertical.CenterVertically,
        horizontalAlignment = Alignment.Horizontal.Start,

        ) {
        Text(
            text = weatherData?.name ?: "null",
            modifier = GlanceModifier,
            style = androidx.glance.text.TextStyle(
                fontSize = 14.sp,
                fontWeight = androidx.glance.text.FontWeight.Bold,
                color = ColorProvider(gold)
            )
        )
        Spacer(modifier = GlanceModifier.size(10.dp))

        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            val temp = weatherData?.main?.temp?.minus(273.15)
            Text(
                text = "${temp?.toInt()}°C\n",
                modifier = GlanceModifier.defaultWeight(),
                style = androidx.glance.text.TextStyle(
                    fontSize = 32.sp,
                    fontWeight = androidx.glance.text.FontWeight.Bold,
                    color = ColorProvider(gold)
                )
            )

            val iconUrl = weatherData?.weather?.firstOrNull()?.getIconUrl()


            LaunchedEffect(iconUrl) {
                if (iconUrl != null) {
                    bitmap = fetchBitmapFromUrl(iconUrl)
                }
            }



            bitmap?.let {
                androidx.glance.Image(
                    modifier = GlanceModifier.width(90.dp).height(90.dp).defaultWeight(),
                    provider = ImageProvider(it),
                    contentDescription = "weather icon"
                )
            }



            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically
            )
            {
                weatherData?.let { data ->
                    val feelsLike = data.main.feels_like - 273.15

                    Text(
                        text = data.weather.firstOrNull()?.main ?: "",
                        modifier = GlanceModifier,
                        style = androidx.glance.text.TextStyle(
                            fontSize = 14.sp,
                            fontWeight = androidx.glance.text.FontWeight.Bold,
                            color = ColorProvider(gold)
                        )
                    )
                    Text(
                        text = "Ощущается как: ${feelsLike.toInt()}°C\n",
                        modifier = GlanceModifier,
                        style = androidx.glance.text.TextStyle(
                            fontSize = 14.sp,
                            fontWeight = androidx.glance.text.FontWeight.Bold,
                            color = ColorProvider(gold)
                        )
                    )
                }
            }

        }

    }
    }
}

@Composable
fun weatherAuthorize() {

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color.Transparent))
            .cornerRadius(25.dp),
        verticalAlignment = Alignment.Vertical.CenterVertically,
        horizontalAlignment = Alignment.Horizontal.Start,
    ) {

        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(ColorProvider(Color.Transparent)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.End
        ) {

            androidx.glance.Image(
                modifier = GlanceModifier
                    .width(30.dp)
                    .height(30.dp)
                    .clickable(actionRunCallback<RefreshWeatherAction>()),
                provider = ImageProvider(R.drawable.refresh),
                contentDescription = "weather icon"
            )
        }
        Column(
            modifier = GlanceModifier
                .background(ColorProvider(jazzblue))
                .cornerRadius(25.dp),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,

            ) {

            Text(
                text = "Пожалуйста, авторизируйтесь чтобы использовать виджет",
                modifier = GlanceModifier,
                style = androidx.glance.text.TextStyle(
                    fontSize = 14.sp,
                    fontWeight = androidx.glance.text.FontWeight.Bold,
                    color = ColorProvider(brown)
                )
            )

        }
    }
}


suspend fun fetchBitmapFromUrl(iconUrl: String?): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL(iconUrl)
            BitmapFactory.decodeStream(url.openConnection().getInputStream())
        } catch (e: Exception) {
            Log.e("WeatherWidget", "Ошибка при загрузке изображения: ${e.message}")
            null
        }
    }
}
