package com.paraskcd.influentiallauncher.startmenu.presentation.windows

import android.view.Gravity
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paraskcd.influentiallauncher.designsystem.molecules.InfSegmented
import com.paraskcd.influentiallauncher.designsystem.theme.InfRadii
import com.paraskcd.influentiallauncher.startmenu.presentation.model.StartMenuTab
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.TabToggles
import com.paraskcd.influentiallauncher.windowing.presentation.InfWindow
import com.paraskcd.influentiallauncher.windowing.presentation.LocalWindowBlurred

@Composable
fun StartTabsWindow(
    open: Boolean,
    offsetY: Dp,
    tabs: List<StartMenuTab>,
    selected: StartMenuTab,
    onSelect: (StartMenuTab) -> Unit,
    onClose: () -> Unit,
    offsetX: Dp = 0.dp
) {
    InfWindow(
        cornerRadius = InfRadii.pill,
        onDismissRequest = onClose,
        gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
        offsetX = offsetX,
        offsetY = offsetY,
        visible = open
    ) {
        InfSegmented(
            labels = tabs.map { stringResource(TabToggles.labelOf(it)) },
            selected = tabs.indexOf(selected).coerceAtLeast(0),
            onSelect = { onSelect(tabs[it]) },
            blurred = LocalWindowBlurred.current
        )
    }
}
