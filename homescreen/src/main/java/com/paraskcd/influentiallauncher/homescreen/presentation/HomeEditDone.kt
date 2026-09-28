// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.homescreen.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paraskcd.influentiallauncher.designsystem.atoms.InfButton
import com.paraskcd.influentiallauncher.homescreen.R
import com.paraskcd.influentiallauncher.homescreen.presentation.viewmodels.EditModeViewModel

/** The Done button that ends edit mode, shown only while home and taskbar wiggle. */
@Composable
fun HomeEditDone(modifier: Modifier = Modifier, viewModel: EditModeViewModel = hiltViewModel()) {
    val active by viewModel.active.collectAsStateWithLifecycle()
    AnimatedVisibility(visible = active, enter = fadeIn(), exit = fadeOut(), modifier = modifier) {
        InfButton(label = stringResource(R.string.home_done), onClick = viewModel::stop)
    }
}
