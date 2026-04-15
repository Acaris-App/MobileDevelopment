package com.acaris

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen // 🌟 IMPORT SPLASH SCREEN
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
        // 🌟 FIX 2: Pasang Splash Screen SEBELUM super.onCreate
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

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

                // 🌟 FIX 3: Tahan Splash Screen di layar sampai startDestination tidak null (selesai loading token)
                splashScreen.setKeepOnScreenCondition {
                    startDestination == null
                }

                startDestination?.let { dest ->
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()

                        NavHost(
                            navController = navController,
                            startDestination = dest
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