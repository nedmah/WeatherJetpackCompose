package com.example.gorbunovmobdev.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(userData: UserData)

    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    suspend fun getUserCount(email: String): Int

    @Query("SELECT COUNT(*) FROM users WHERE email = :email AND password = :password")
    suspend fun getUserCountByEmailAndPassword(email: String, password: String): Int
}