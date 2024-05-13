package com.example.gorbunovmobdev

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherWidgetAlarmReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "com.example.gorbunovmobdev.UPDATE_WIDGET") {
            val scope = CoroutineScope(Dispatchers.Main)
            scope.launch {
                context?.let { WeatherWidget().updateAll(it) }
            }
        }
    }

}