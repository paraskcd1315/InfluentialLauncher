package com.paraskcd.influentiallauncher.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.paraskcd.influentiallauncher.viewmodels.WeatherViewModel

@Composable
fun WeatherMediaWidget(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        WeatherWidget()
    }
}