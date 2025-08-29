package com.paraskcd.influentiallauncher.ui.theme.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.paraskcd.influentiallauncher.dialogs.StartMenuDialog
import com.paraskcd.influentiallauncher.ui.theme.icons.WindowsIcon
import com.paraskcd.influentiallauncher.ui.theme.icons.WindowsOpenedIcon

@Composable
fun BottomDock(modifier: Modifier = Modifier, context: Context) {
    val isOpen by StartMenuDialog.isOpenFlow.collectAsState()

    Box(
        modifier = modifier
            .padding(24.dp)
            .fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = {
                    if (isOpen) {
                        StartMenuDialog.close()
                    } else {
                        StartMenuDialog.showOrUpdate(context)
                    }
                },
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isOpen)
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                        else
                            Color.Transparent
                    )
                    .border(
                        width = 1.dp,
                        color = if (isOpen)
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                                else
                                    Color.Transparent,
                        shape = RoundedCornerShape(16.dp))
            ) {
                Icon(
                    imageVector =
                        if (isOpen) {
                            WindowsOpenedIcon
                        } else {
                            WindowsIcon
                        },
                    contentDescription = "Menú",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}