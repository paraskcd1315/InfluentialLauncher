package com.paraskcd.influentiallauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.paraskcd.influentiallauncher.dialogs.DockDialog
import com.paraskcd.influentiallauncher.dialogs.StartMenuDialog
import com.paraskcd.influentiallauncher.ui.theme.InfluentialLauncherTheme
import com.paraskcd.influentiallauncher.ui.theme.screens.LauncherScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InfluentialLauncherTheme {
                LauncherScreen()
            }
        }
    }
}