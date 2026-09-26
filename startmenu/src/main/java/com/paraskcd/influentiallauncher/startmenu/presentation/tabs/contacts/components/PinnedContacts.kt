package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.contacts.components

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.paraskcd.influentiallauncher.contacts.domain.model.Contact
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PinnedContacts(
    contacts: List<Contact>,
    loadPhoto: suspend (Contact, Int) -> Bitmap?,
    onOpen: (Contact) -> Unit,
    onLongPress: (Contact) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        contacts.chunked(StartMenuMetrics.pinnedColumns).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { contact ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(StartMenuMetrics.pinnedLabelGap),
                        modifier = Modifier
                            .weight(1f)
                            .clip(InfShapes.md)
                            .combinedClickable(
                                onClickLabel = contact.name,
                                onClick = { onOpen(contact) },
                                onLongClick = { onLongPress(contact) }
                            )
                            .padding(StartMenuMetrics.pinnedCellPadding)
                    ) {
                        ContactAvatar(contact = contact, loadPhoto = loadPhoto)
                        Text(
                            text = contact.name,
                            style = MaterialTheme.typography.labelMedium,
                            color = InfTheme.colors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                repeat(StartMenuMetrics.pinnedColumns - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}
