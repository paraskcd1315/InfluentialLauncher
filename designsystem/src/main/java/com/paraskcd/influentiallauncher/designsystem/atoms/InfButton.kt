package com.paraskcd.influentiallauncher.designsystem.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme

@Composable
fun InfButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(DsMetrics.buttonHeight)
            .alpha(if (enabled) 1f else DsMetrics.disabledAlpha)
            .clip(InfShapes.pill)
            .background(InfTheme.colors.brand)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = InfSpacing.s5)
    ) {
        Text(text = label, fontSize = DsMetrics.buttonTextSize, fontWeight = FontWeight.SemiBold, color = InfTheme.colors.onBrand, maxLines = 1)
    }
}
