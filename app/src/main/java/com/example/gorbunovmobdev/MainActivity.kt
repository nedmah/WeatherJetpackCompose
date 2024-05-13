package com.example.gorbunovmobdev

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gorbunovmobdev.ui.theme.GorbunovMobDevTheme
import com.example.gorbunovmobdev.ui.theme.gold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GorbunovMobDevTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    page()
                }
            }
        }
    }
}



//@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignIn(){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val userViewModel : UserViewModel = viewModel(factory = UserViewModel.factory)
    val scope = rememberCoroutineScope()
    var loggedIn by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)

    if (sharedPreferences.getBoolean("loggedIn", false)) context.startActivity(GreetingsActivity::class.java)



    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            value = email,
            onValueChange = {email = it.take(18)},
            label = { Text(text = "Почта") },
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
        Spacer(modifier = Modifier.height(30.dp))
        TextField(
            value =password,
            onValueChange = {password = it.take(18)},
            label = { Text(text = "Пароль") },
            modifier = Modifier.border(
                shape = RoundedCornerShape(28.dp),
                width = 2.dp,
                color = gold
            ),
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black,
            ),
        )
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = {

                scope.launch(Dispatchers.IO) {
                    loggedIn = userViewModel.login(email, password)

                    if (loggedIn) {
                        sharedPreferences.edit().putBoolean("loggedIn", true).apply()
                        context.startActivity(GreetingsActivity::class.java)
                    } else{
                        scope.launch(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Неверная почта/пароль",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }
                }
                      },
            modifier = Modifier
        ) {
            Text(text = "Войти")
        }
    }


}


@OptIn(ExperimentalMaterial3Api::class)
//@Preview(showBackground = true)
@Composable
fun SignUp(){
    val userViewModel : UserViewModel = viewModel(factory = UserViewModel.factory)
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var password_confirm by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            value = email,
            onValueChange = {email = it.take(18)},
            label = { Text(text = "Почта") },
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
        TextField(
            value =password,
            onValueChange = {password = it.take(18)},
            label = { Text(text = "Пароль") },
            modifier = Modifier.border(
                shape = RoundedCornerShape(28.dp),
                width = 2.dp,
                color = gold
            ),
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black,
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            value =password_confirm,
            onValueChange = {password_confirm = it.take(18)},
            label = { Text(text = "Подтвердите пароль") },
            modifier = Modifier.border(
                shape = RoundedCornerShape(28.dp),
                width = 2.dp,
                color = gold
            ),
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black,
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                if(password == password_confirm){
                    scope.launch(Dispatchers.IO) {
                    if (!userViewModel.isUserExists(email)){
                        userViewModel.registerUser(email,password)
                        withContext(Dispatchers.Default){
                            Looper.prepare()
                            Toast.makeText(
                                context,
                                "Успешно",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else{
                        scope.launch(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Такой пользователь уже существует",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    }

                }

                      },
            modifier = Modifier
        ) {
            Text(text = "Зарегистрироваться")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun page(){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.picture) ,
            contentDescription = "image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.width(180.dp)
        )

        val pagerState = rememberPagerState{
            2
        }
        val tabs = listOf("SignIn", "SignUp")
        val scope = rememberCoroutineScope()

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            Modifier.background(colorResource(id = R.color.purple_200))
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = pagerState.currentPage == index,
                    onClick =  {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) { page ->
            when (page) {
                0 -> SignIn()
                1 -> SignUp()
                else -> throw IllegalArgumentException("Invalid page: $page")
            }
        }

    }
}


fun Context.startActivity(activityClass: Class<*>) {
    val intent = Intent(this, activityClass)
    startActivity(intent)
}
