package com.arrterm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.arrterm.ui.nav.AppNavHost
import com.arrterm.ui.theme.ArrTermTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = (application as ArrTermApplication).container.serverConfigRepository
        setContent {
            ArrTermTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(repository)
                }
            }
        }
    }
}
