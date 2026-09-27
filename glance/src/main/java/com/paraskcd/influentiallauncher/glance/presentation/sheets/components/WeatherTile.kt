package com.paraskcd.influentiallauncher.glance.presentation.sheets.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import com.paraskcd.influentiallauncher.designsystem.foundation.infGlassSurface
import com.paraskcd.influentiallauncher.designsystem.theme.InfRadii
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.glance.presentation.utils.WeatherSheetMetrics

@Composable
fun WeatherTile(
    icon: ImageVector,
    label: String,
    value: String?,
    modifier: Modifier = Modifier,
    detail: String? = null
) {
    val colors = InfTheme.colors
    Column(
        verticalArrangement = Arrangement.spacedBy(InfSpacing.s1),
        modifier = modifier
            .infGlassSurface(RoundedCornerShape(InfRadii.md), specular = false)
            .padding(InfSpacing.s3)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(InfSpacing.s1), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(WeatherSheetMetrics.statIcon))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = value ?: "–",
            style = MaterialTheme.typography.titleLarge,
            color = colors.textPrimary,
            maxLines = 1
        )
        detail?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
