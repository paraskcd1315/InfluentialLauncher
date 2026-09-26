package com.paraskcd.influentiallauncher.startmenu.presentation.components.StartMenu

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.designsystem.atoms.InfSearchField
import com.paraskcd.influentiallauncher.designsystem.foundation.infPanelSurface
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.components.StartMenu.components.AlphabetGrid
import com.paraskcd.influentiallauncher.startmenu.presentation.components.StartMenu.components.AppList
import com.paraskcd.influentiallauncher.startmenu.presentation.components.StartMenu.components.AppListSkeleton
import com.paraskcd.influentiallauncher.startmenu.presentation.model.AppSection
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.AppSections
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics
import com.paraskcd.influentiallauncher.windowing.presentation.LocalWindowBlurred
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StartMenu(
    sections: List<AppSection>?,
    query: String,
    onQueryChange: (String) -> Unit,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    onLaunch: (AppId, Rect?) -> Unit,
    onTogglePin: (AppId) -> Unit,
    onInfo: (AppId, Rect?) -> Unit,
    onUninstall: (AppId) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var alphabetOpen by remember { mutableStateOf(false) }
    var expandedKey by remember { mutableStateOf<String?>(null) }
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val fraction = if (WindowInsets.isImeVisible) StartMenuMetrics.listHeightImeFraction else StartMenuMetrics.listHeightFraction
    val listHeight = screenHeight * fraction

    LaunchedEffect(query) {
        expandedKey = null
        alphabetOpen = false
        listState.scrollToItem(0)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .infPanelSurface(RoundedCornerShape(StartMenuMetrics.cornerRadius), blurred = LocalWindowBlurred.current)
            .padding(StartMenuMetrics.padding)
    ) {
        InfSearchField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = stringResource(R.string.startmenu_search),
            clearDescription = stringResource(R.string.startmenu_clear)
        )
        Spacer(Modifier.height(StartMenuMetrics.searchGap))
        val listModifier = Modifier
            .fillMaxWidth()
            .height(listHeight)
        when {
            sections == null -> AppListSkeleton(modifier = listModifier)
            sections.isEmpty() -> Text(
                text = stringResource(R.string.startmenu_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = InfTheme.colors.textSecondary,
                modifier = listModifier.padding(StartMenuMetrics.rowPadding)
            )
            alphabetOpen -> AlphabetGrid(
                available = sections.map { it.letter }.toSet(),
                onPick = { letter ->
                    alphabetOpen = false
                    val index = AppSections.headerIndices(sections)[letter] ?: return@AlphabetGrid
                    scope.launch { listState.scrollToItem(index) }
                },
                modifier = listModifier
            )
            else -> AppList(
                sections = sections,
                state = listState,
                expandedKey = expandedKey,
                loadIcon = loadIcon,
                onHeaderClick = { alphabetOpen = true },
                onLaunch = onLaunch,
                onExpand = { expandedKey = it },
                onTogglePin = onTogglePin,
                onInfo = onInfo,
                onUninstall = onUninstall,
                modifier = listModifier
            )
        }
    }
}
