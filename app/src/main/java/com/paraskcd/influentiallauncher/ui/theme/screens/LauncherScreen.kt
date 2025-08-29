package com.paraskcd.influentiallauncher.ui.theme.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.paraskcd.influentiallauncher.dialogs.DockDialog

@Composable
fun LauncherScreen() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        DockDialog.showOrUpdate(context = context)
    }

    Scaffold(
        containerColor = Color.Transparent
    ) { inner ->
        Column(modifier = Modifier.padding(inner)) {  }
    }
}