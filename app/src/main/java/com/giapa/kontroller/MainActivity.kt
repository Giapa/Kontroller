package com.giapa.kontroller

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.giapa.kontroller.navigation.AppNavHost
import com.giapa.kontroller.ui.theme.KOntrollerTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KOntrollerTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppPreview() {
    KOntrollerTheme {
        val navController = rememberNavController()
        Scaffold(modifier = Modifier.fillMaxSize()) {
            AppNavHost(navController = navController)
        }
    }
}