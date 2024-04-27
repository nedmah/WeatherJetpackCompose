package com.example.gorbunovmobdev

import android.app.Application
import com.example.gorbunovmobdev.data.MainDb

class App : Application(){

    val database by lazy { MainDb.createDB(this) }
}