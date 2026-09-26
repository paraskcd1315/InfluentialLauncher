package com.paraskcd.influentiallauncher.taskbar.presentation.components.AppPicker

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.X
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.designsystem.atoms.InfTile
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.foundation.infPanelSurface
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.taskbar.R
import com.paraskcd.influentiallauncher.taskbar.presentation.components.AppPicker.components.AppPickerRow
import com.paraskcd.influentiallauncher.taskbar.presentation.components.AppPicker.components.AppPickerSkeleton
import com.paraskcd.influentiallauncher.taskbar.presentation.model.PickerRow
import com.paraskcd.influentiallauncher.taskbar.presentation.utils.TaskbarMetrics
import com.paraskcd.influentiallauncher.windowing.presentation.LocalWindowBlurred

@Composable
fun AppPicker(
    rows: List<PickerRow>?,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    onToggle: (AppId) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .infPanelSurface(RoundedCornerShape(TaskbarMetrics.pickerCornerRadius), blurred = LocalWindowBlurred.current)
            .padding(TaskbarMetrics.pickerPadding)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.picker_title),
                style = MaterialTheme.typography.titleMedium,
                color = InfTheme.colors.textPrimary,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = InfSpacing.s3)
            )
            InfTile(onClick = onClose, contentDescription = stringResource(R.string.picker_close)) {
                Icon(
                    imageVector = Lucide.X,
                    contentDescription = stringResource(R.string.picker_close),
                    tint = InfTheme.colors.textSecondary,
                    modifier = Modifier.size(DsMetrics.glyphSize)
                )
            }
        }
        when {
            rows == null -> AppPickerSkeleton()
            rows.isEmpty() -> Text(
                text = stringResource(R.string.picker_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = InfTheme.colors.textSecondary,
                modifier = Modifier.padding(InfSpacing.s3)
            )
            else -> LazyColumn(modifier = Modifier.heightIn(max = TaskbarMetrics.pickerMaxHeight)) {
                items(rows, key = { it.app.id.key }) { row ->
                    AppPickerRow(row = row, loadIcon = loadIcon, onToggle = onToggle)
                }
            }
        }
    }
}
