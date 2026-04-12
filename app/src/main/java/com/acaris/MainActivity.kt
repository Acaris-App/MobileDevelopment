package com.acaris

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.acaris.core.navigation.Screen
import com.acaris.core.navigation.authNavGraph
import com.acaris.core.navigation.mainNavGraph
import com.acaris.core.network.datastore.AuthPreferences
import com.acaris.core.ui.theme.AcarisTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authPreferences: AuthPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AcarisTheme {
                var startDestination by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    authPreferences.getAuthToken().collect { token ->
                        if (startDestination == null) {
                            startDestination = if (token.isNullOrBlank()) Screen.Welcome.route else Screen.MainApp.route
                        }
                    }
                }

                if (startDestination == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        val navController = rememberNavController()

                        NavHost(
                            navController = navController,
                            startDestination = startDestination!!,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            authNavGraph(navController)
                            mainNavGraph(navController)
                        }
                    }
                }
            }
        }
    }
}