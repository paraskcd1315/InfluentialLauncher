package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paraskcd.influentiallauncher.designsystem.atoms.InfSectionHeader
import com.paraskcd.influentiallauncher.designsystem.atoms.InfSwitch
import com.paraskcd.influentiallauncher.designsystem.molecules.InfGroupedCard
import com.paraskcd.influentiallauncher.designsystem.molecules.InfSettingsRow
import com.paraskcd.influentiallauncher.settings.domain.model.LauncherSettings
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.model.StartMenuTab
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.TabToggles

@Composable
fun SettingsTab(
    settings: LauncherSettings,
    onTabShown: (StartMenuTab, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val toggles = TabToggles.of(settings)
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(StartMenuMetrics.rowGap),
        contentPadding = PaddingValues(
            start = StartMenuMetrics.listPadding,
            end = StartMenuMetrics.listPadding,
            top = StartMenuMetrics.listTopPlain,
            bottom = StartMenuMetrics.listBottom
        ),
        modifier = modifier.fillMaxSize()
    ) {
        item { InfSectionHeader(text = stringResource(R.string.startmenu_settings_section)) }
        itemsIndexed(toggles, key = { _, toggle -> toggle.tab.name }) { index, toggle ->
            InfGroupedCard(index = index, count = toggles.size) {
                InfSettingsRow(
                    label = stringResource(toggle.labelRes),
                    caption = stringResource(R.string.startmenu_settings_caption),
                    trailing = { InfSwitch(checked = toggle.shown, onCheckedChange = { onTabShown(toggle.tab, it) }) }
                )
            }
        }
    }
}
