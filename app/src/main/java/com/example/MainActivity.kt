package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.CosmicSlate
import com.example.viewmodel.SwitchRomViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: SwitchRomViewModel by viewModels()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                var currentScreen by remember { mutableStateOf("dashboard") }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CosmicSlate)
                        .testTag("main_scaffold")
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(CosmicSlate)
                    ) {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                            },
                            label = "screen_navigation"
                        ) { screen ->
                            when (screen) {
                                "dashboard" -> DashboardScreen(
                                    viewModel = viewModel,
                                    onNavigate = { currentScreen = it }
                                )
                                "converter" -> ConverterScreen(
                                    viewModel = viewModel,
                                    onBack = { currentScreen = "dashboard" }
                                )
                                "optimizer" -> OptimizerScreen(
                                    viewModel = viewModel,
                                    onBack = { currentScreen = "dashboard" }
                                )
                                "keys" -> KeysHubScreen(
                                    viewModel = viewModel,
                                    onBack = { currentScreen = "dashboard" }
                                )
                                "drivers" -> DriverManagerScreen(
                                    viewModel = viewModel,
                                    onBack = { currentScreen = "dashboard" }
                                )
                                "library" -> LibraryScreen(
                                    viewModel = viewModel,
                                    onBack = { currentScreen = "dashboard" }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
