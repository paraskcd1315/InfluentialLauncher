package com.paraskcd.influentiallauncher.designsystem.organisms

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.material3.Icon
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.Lucide
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.foundation.infClickableQuiet
import com.paraskcd.influentiallauncher.designsystem.foundation.infGlassSurface
import com.paraskcd.influentiallauncher.designsystem.theme.InfMotion
import com.paraskcd.influentiallauncher.designsystem.theme.InfRadii
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InfBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String,
    leading: (@Composable () -> Unit)? = null,
    edgeToEdge: Boolean = false,
    header: (@Composable ColumnScope.() -> Unit)? = null,
    onTitleClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = InfTheme.colors
    val shape = RoundedCornerShape(topStart = InfRadii.xl, topEnd = InfRadii.xl)
    val scrimState = remember { MutableTransitionState(false) }.apply { targetState = visible }
    val panelState = remember { MutableTransitionState(false) }.apply { targetState = visible }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visibleState = scrimState,
            enter = fadeIn(tween(InfMotion.durMorphMs, easing = InfMotion.easeIos)),
            exit = fadeOut(tween(InfMotion.durMorphMs, easing = InfMotion.easeIos))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.scrim)
                    .infClickableQuiet(onDismiss)
            )
        }
        AnimatedVisibility(
            visibleState = panelState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.statusBarsIgnoringVisibility)
                .padding(top = InfSpacing.s2),
            enter = slideInVertically(tween(InfMotion.durPushMs, easing = InfMotion.easeIos)) { it } +
                fadeIn(tween(InfMotion.durMorphMs)),
            exit = slideOutVertically(tween(InfMotion.durPushMs, easing = InfMotion.easeIos)) { it } +
                fadeOut(tween(InfMotion.durMorphMs))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .infGlassSurface(shape, strong = true)
                    .infClickableQuiet {}
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = InfSpacing.s5, end = InfSpacing.s5, bottom = InfSpacing.s4),
                    verticalArrangement = Arrangement.spacedBy(InfSpacing.s4)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = InfSpacing.s2)
                            .size(DsMetrics.sheetHandleWidth, DsMetrics.sheetHandleHeight)
                            .clip(InfShapes.pill)
                            .background(colors.border)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(InfSpacing.s3),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        leading?.invoke()
                        Box(modifier = Modifier.weight(1f)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(InfSpacing.s1),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = if (onTitleClick != null) Modifier.clip(InfShapes.md).clickable(onClick = onTitleClick) else Modifier
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = colors.textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                if (onTitleClick != null) {
                                    Icon(
                                        imageVector = Lucide.ChevronDown,
                                        contentDescription = null,
                                        tint = colors.textSecondary,
                                        modifier = Modifier.size(DsMetrics.actionIconSize)
                                    )
                                }
                            }
                        }
                        trailing?.invoke()
                    }
                    header?.invoke(this)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(DsMetrics.hairlineThickness)
                        .background(colors.hairline)
                )
                Column(
                    modifier = if (edgeToEdge) {
                        Modifier
                            .weight(1f, fill = false)
                            .fillMaxWidth()
                    } else {
                        Modifier
                            .weight(1f, fill = false)
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
                            .padding(InfSpacing.s5)
                    },
                    content = content
                )
            }
        }
    }
}
