package com.example.gorbunovmobdev.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "users")
data class UserData(
    @PrimaryKey(autoGenerate = true)
    val id : Int? = null,
    val email : String,
    val password : String

)
