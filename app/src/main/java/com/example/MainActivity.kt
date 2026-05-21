package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.SportColors
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CricketViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = SportColors.DarkBackground
                ) {
                    val viewModel: CricketViewModel = viewModel()
                    var isLoggedIn by remember { mutableStateOf(false) }

                    if (!isLoggedIn) {
                        LoginScreen(
                            viewModel = viewModel,
                            onLoginSuccess = { isLoggedIn = true }
                        )
                    } else {
                        DashboardScreen(
                            viewModel = viewModel,
                            onLogout = { isLoggedIn = false }
                        )
                    }
                }
            }
        }
    }
}
