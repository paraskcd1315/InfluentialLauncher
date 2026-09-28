// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.designsystem.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme

@Composable
fun InfCountBadge(
    count: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    size: Dp = DsMetrics.countBadgeSize,
    textSize: TextUnit = DsMetrics.countBadgeTextSize,
    background: Color = InfTheme.colors.brand,
    content: Color = InfTheme.colors.onBrand,
    ring: Color = Color(DsMetrics.countBadgeRingArgb)
) {
    val label = if (count > DsMetrics.countBadgeMax) "${DsMetrics.countBadgeMax}+" else count.toString()
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(background)
            .border(maxOf(size * DsMetrics.countBadgeRingFraction, DsMetrics.hairlineThickness), ring, CircleShape)
            .semantics { contentDescription?.let { this.contentDescription = it } }
    ) {
        Text(text = label, fontSize = textSize, fontWeight = FontWeight.SemiBold, color = content, maxLines = 1)
    }
}
