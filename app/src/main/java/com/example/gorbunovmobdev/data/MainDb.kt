package com.example.gorbunovmobdev.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserData::class
    ],
    version = 1
)
abstract class MainDb : RoomDatabase(){

    abstract val dao : UserDao

    companion object{
        fun createDB(context: Context) : MainDb {
            return Room.databaseBuilder(
                context,
                MainDb::class.java,
                "ComposeDB"
            ).build()
        }
    }

}