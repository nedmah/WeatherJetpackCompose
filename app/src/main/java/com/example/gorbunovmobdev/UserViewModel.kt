package com.example.gorbunovmobdev

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.gorbunovmobdev.data.MainDb
import com.example.gorbunovmobdev.data.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

@Suppress("UNCHECKED_CAST")
class UserViewModel(val database: MainDb) : ViewModel(){



    suspend fun isUserExists(email : String) : Boolean{
        return withContext(Dispatchers.IO){
            val count = database.dao.getUserCount(email)
            count > 0
        }
    }


    fun registerUser(email: String, password : String) = viewModelScope.launch(Dispatchers.IO) {
            val user = UserData(null,email,password)
            database.dao.insertUser(user)
    }

    suspend fun login(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO){
            val count = database.dao.getUserCount(email)
            count > 0
        }
    }



    companion object{
        val factory : ViewModelProvider.Factory = object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras): T {
                val database = (checkNotNull(extras[APPLICATION_KEY]) as App).database
                return UserViewModel(database) as T
            }
        }
    }

}