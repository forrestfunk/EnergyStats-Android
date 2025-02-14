package com.alpriest.energystats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appContainer = (application as EnergyStatsApplication).appContainer
        val preHomeViewModel = PreHomeViewModel(appContainer.networking, appContainer.configManager, appContainer.userManager)

        setContent {
            val systemUiController = rememberSystemUiController()
            if (isSystemInDarkTheme()) {
                SideEffect {
                    systemUiController.setSystemBarsColor(Color.Black, darkIcons = false)
                }
            }

            PreHomeView(appContainer = appContainer, viewModel = preHomeViewModel)
        }
    }
}
