package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.contacts.components

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MessageCircle
import com.composables.icons.lucide.Phone
import com.paraskcd.influentiallauncher.contacts.domain.model.Contact
import com.paraskcd.influentiallauncher.designsystem.atoms.InfIconButton
import com.paraskcd.influentiallauncher.designsystem.molecules.InfGroupedCard
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactRow(
    contact: Contact,
    index: Int,
    count: Int,
    loadPhoto: suspend (Contact, Int) -> Bitmap?,
    onOpen: (Contact) -> Unit,
    onLongPress: (Contact) -> Unit,
    onCall: (Contact) -> Unit,
    onWhatsApp: (Contact) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = InfTheme.colors
    InfGroupedCard(index = index, count = count, modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(StartMenuMetrics.rowIconGap),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClickLabel = contact.name,
                    onClick = { onOpen(contact) },
                    onLongClick = { onLongPress(contact) }
                )
                .padding(StartMenuMetrics.rowPadding)
        ) {
            ContactAvatar(contact = contact, loadPhoto = loadPhoto)
            Text(
                text = contact.name,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(StartMenuMetrics.contactActionGap)) {
                InfIconButton(
                    icon = Lucide.Phone,
                    contentDescription = stringResource(R.string.startmenu_call),
                    tint = colors.textPrimary,
                    enabled = contact.phone != null,
                    onClick = { onCall(contact) }
                )
                InfIconButton(
                    icon = Lucide.MessageCircle,
                    contentDescription = stringResource(R.string.startmenu_whatsapp),
                    tint = colors.success,
                    enabled = contact.canWhatsApp,
                    onClick = { onWhatsApp(contact) }
                )
            }
        }
    }
}
