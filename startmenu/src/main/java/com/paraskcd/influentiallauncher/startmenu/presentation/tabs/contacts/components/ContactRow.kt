package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.contacts.components

import android.graphics.Bitmap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.paraskcd.influentiallauncher.contacts.domain.model.Contact
import com.paraskcd.influentiallauncher.designsystem.molecules.InfGroupedCard
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics

@Composable
fun ContactRow(
    contact: Contact,
    index: Int,
    count: Int,
    loadPhoto: suspend (Contact, Int) -> Bitmap?,
    onOpen: (Contact) -> Unit,
    modifier: Modifier = Modifier
) {
    InfGroupedCard(index = index, count = count, modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(StartMenuMetrics.rowIconGap),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClickLabel = contact.name) { onOpen(contact) }
                .padding(StartMenuMetrics.rowPadding)
        ) {
            ContactAvatar(contact = contact, loadPhoto = loadPhoto)
            Text(
                text = contact.name,
                style = MaterialTheme.typography.bodyLarge,
                color = InfTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
