package com.ndk.playground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ndk.playground.screen.bitmap.BitmapScreen
import com.ndk.playground.screen.landing.LandingScreen
import com.ndk.playground.ui.theme.NDKPlaygroundTheme
import com.ndk.playground.utils.ImageProcessor
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var imageProcessor: ImageProcessor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NDKPlaygroundTheme {
                MainApp(
                    navController = rememberNavController(),
                    imageProcessor = imageProcessor
                )
            }
        }
    }
}

@Composable
private fun MainApp(
    navController: NavHostController,
    imageProcessor: ImageProcessor
) {
    NavHost(
        navController = navController,
        startDestination = NdkPlaygroundScreen.LANDING.name
    ) {
        composable(NdkPlaygroundScreen.LANDING.name) {
            LandingScreen(
                onBack = navController::popBackStack,
                onBitmapClicked = {
                    navController.navigate(NdkPlaygroundScreen.BITMAP.name)
                }
            )
        }
        composable(NdkPlaygroundScreen.BITMAP.name) {
            BitmapScreen(
                onBack = navController::popBackStack,
                imageProcessor = imageProcessor
            )
        }
    }
}
