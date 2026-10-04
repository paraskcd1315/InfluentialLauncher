// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.designsystem.atoms

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.foundation.infGlassSurface
import com.paraskcd.influentiallauncher.designsystem.foundation.infParallaxLayer
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme

@Composable
fun InfTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    secret: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val colors = InfTheme.colors
    val textStyle = TextStyle(fontSize = DsMetrics.settingsItemTextSize, color = colors.textPrimary)
    Column(
        modifier = modifier
            .infParallaxLayer()
            .fillMaxWidth()
            .heightIn(min = DsMetrics.settingsRowHeight)
            .infGlassSurface(InfShapes.md, specular = false, strong = true)
            .padding(horizontal = InfSpacing.s4, vertical = InfSpacing.s2)
    ) {
        Text(text = label, fontSize = DsMetrics.settingsCaptionTextSize, fontWeight = FontWeight.SemiBold, color = colors.textSecondary)
        Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.fillMaxWidth().padding(top = InfSpacing.s1)) {
            if (value.isEmpty() && placeholder.isNotEmpty()) {
                Text(text = placeholder, style = textStyle, color = colors.textTertiary, maxLines = 1)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = textStyle,
                cursorBrush = SolidColor(colors.textPrimary),
                visualTransformation = if (secret) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (secret) KeyboardType.Password else keyboardType,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
