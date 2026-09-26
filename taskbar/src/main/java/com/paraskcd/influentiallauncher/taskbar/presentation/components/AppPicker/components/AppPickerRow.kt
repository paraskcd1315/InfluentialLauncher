package com.paraskcd.influentiallauncher.taskbar.presentation.components.AppPicker.components

import android.graphics.Bitmap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.taskbar.R
import com.paraskcd.influentiallauncher.taskbar.presentation.components.AppIcon
import com.paraskcd.influentiallauncher.taskbar.presentation.model.PickerRow
import com.paraskcd.influentiallauncher.taskbar.presentation.utils.TaskbarMetrics

@Composable
fun AppPickerRow(
    row: PickerRow,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    onToggle: (AppId) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(InfSpacing.s3),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = TaskbarMetrics.pickerRowHeight)
            .clip(InfShapes.md)
            .clickable { onToggle(row.app.id) }
            .padding(horizontal = InfSpacing.s3)
    ) {
        AppIcon(id = row.app.id, size = TaskbarMetrics.pickerIconSize, loadIcon = loadIcon)
        Text(
            text = row.app.label,
            style = MaterialTheme.typography.bodyLarge,
            color = InfTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        if (row.pinned) {
            Icon(
                imageVector = Lucide.Check,
                contentDescription = stringResource(R.string.picker_pinned),
                tint = InfTheme.colors.brandText,
                modifier = Modifier.size(TaskbarMetrics.pickerCheckSize)
            )
        }
    }
}
