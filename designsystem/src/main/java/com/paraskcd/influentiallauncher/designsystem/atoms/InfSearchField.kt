// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.designsystem.atoms

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.X
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.foundation.LocalInfBlurred
import com.paraskcd.influentiallauncher.designsystem.foundation.infPanelSurface
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme

@Composable
fun InfSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    clearDescription: String,
    modifier: Modifier = Modifier,
    onSearch: () -> Unit = { },
    shape: Shape = InfShapes.pill,
    height: Dp = DsMetrics.searchHeight
) {
    val colors = InfTheme.colors
    val textStyle = MaterialTheme.typography.bodyLarge.copy(color = colors.textPrimary)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .infPanelSurface(shape, blurred = LocalInfBlurred.current)
    ) {
        Icon(
            imageVector = Lucide.Search,
            contentDescription = null,
            tint = colors.textTertiary,
            modifier = Modifier
                .padding(start = DsMetrics.searchIconStart, end = DsMetrics.searchIconEnd)
                .size(DsMetrics.searchIconSize)
        )
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (value.isEmpty()) {
                Text(text = placeholder, style = textStyle, color = colors.textTertiary, maxLines = 1)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = textStyle,
                cursorBrush = SolidColor(colors.textPrimary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (value.isNotEmpty()) {
            InfTile(
                onClick = { onValueChange("") },
                contentDescription = clearDescription,
                shape = InfShapes.pill,
                modifier = Modifier.padding(end = DsMetrics.searchClearEnd)
            ) {
                Icon(
                    imageVector = Lucide.X,
                    contentDescription = clearDescription,
                    tint = colors.textPrimary,
                    modifier = Modifier.size(DsMetrics.searchIconSize)
                )
            }
        }
    }
}
