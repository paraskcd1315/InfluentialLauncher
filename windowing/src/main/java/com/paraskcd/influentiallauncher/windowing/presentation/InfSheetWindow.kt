package com.paraskcd.influentiallauncher.windowing.presentation

import android.view.Gravity
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.paraskcd.influentiallauncher.designsystem.organisms.InfBottomSheet
import com.paraskcd.influentiallauncher.designsystem.theme.InfMotion
import kotlinx.coroutines.delay

@Composable
fun <T : Any> InfSheetWindow(
    item: T?,
    title: @Composable (T) -> String,
    onDismiss: () -> Unit,
    leading: (@Composable (T) -> Unit)? = null,
    edgeToEdge: Boolean = false,
    header: (@Composable ColumnScope.(T) -> Unit)? = null,
    onTitleClick: ((T) -> Unit)? = null,
    trailing: (@Composable (T) -> Unit)? = null,
    content: @Composable ColumnScope.(T) -> Unit
) {
    var retained by remember { mutableStateOf(item) }
    var open by remember { mutableStateOf(false) }
    LaunchedEffect(item) {
        if (item != null) {
            retained = item
            open = true
        } else {
            open = false
            delay(InfMotion.durPushMs.toLong())
            retained = null
        }
    }
    val current = retained ?: return
    InfWindow(
        cornerRadius = 0.dp,
        onDismissRequest = onDismiss,
        gravity = Gravity.TOP or Gravity.START,
        visible = open,
        focusable = true,
        fullScreen = true
    ) {
        InfBottomSheet(
            visible = open,
            onDismiss = onDismiss,
            title = title(current),
            leading = leading?.let { { it(current) } },
            edgeToEdge = edgeToEdge,
            header = header?.let { slot -> { slot(current) } },
            onTitleClick = onTitleClick?.let { click -> { click(current) } },
            trailing = trailing?.let { slot -> { slot(current) } }
        ) {
            content(current)
        }
    }
}
