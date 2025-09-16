package com.paraskcd.influentiallauncher.utils

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.ReorderableItemCustom(
    reorderableState: org.burnoutcrew.reorderable.ReorderableState<*>,
    key: Any?,
    modifier: Modifier = Modifier,
    index: Int? = null,
    orientationLocked: Boolean = false,
    content: @Composable BoxScope.(isDragging: Boolean) -> Unit,
) = org.burnoutcrew.reorderable.ReorderableItem(
    reorderableState,
    key,
    modifier,
    Modifier.animateItem(),
    orientationLocked,
    index,
    content
)