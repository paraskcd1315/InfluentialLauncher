package com.paraskcd.influentiallauncher.startmenu.presentation.shared.sheets

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
import com.paraskcd.influentiallauncher.windowing.presentation.InfWindow
import kotlinx.coroutines.delay

@Composable
fun <T : Any> SheetWindow(
    item: T?,
    title: (T) -> String,
    onDismiss: () -> Unit,
    leading: (@Composable (T) -> Unit)? = null,
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
            leading = leading?.let { { it(current) } }
        ) {
            content(current)
        }
    }
}
