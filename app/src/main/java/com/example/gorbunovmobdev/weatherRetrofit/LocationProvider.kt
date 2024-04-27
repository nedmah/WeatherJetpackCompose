package com.example.gorbunovmobdev.weatherRetrofit

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat

class LocationProvider(private val context: Context) {

    var latitude: Double = 0.0
    var longitude: Double = 0.0


    fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Если разрешения не получены
            return
        }

        // Получаем менеджер местоположений
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Получаем последнее известное местоположение
        val lastKnownLocation: Location? =
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        // Проверяем, получено ли местоположение
        if (lastKnownLocation != null) {
            // Получаем долготу и широту из местоположения
            latitude = lastKnownLocation.latitude
            longitude = lastKnownLocation.longitude
        }
    }

}