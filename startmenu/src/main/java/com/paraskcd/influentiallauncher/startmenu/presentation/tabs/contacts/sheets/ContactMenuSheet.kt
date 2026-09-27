package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.contacts.sheets

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MessageCircle
import com.composables.icons.lucide.Phone
import com.composables.icons.lucide.Star
import com.composables.icons.lucide.StarOff
import com.composables.icons.lucide.UserRound
import com.paraskcd.influentiallauncher.contacts.domain.model.Contact
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.designsystem.organisms.InfAction
import com.paraskcd.influentiallauncher.designsystem.organisms.InfActionList
import com.paraskcd.influentiallauncher.windowing.presentation.InfSheetWindow
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.contacts.components.ContactAvatar

@Composable
fun ContactMenuSheet(
    contact: Contact?,
    loadPhoto: suspend (Contact, Int) -> Bitmap?,
    onDismiss: () -> Unit,
    onToggleFavourite: (Contact) -> Unit,
    onCall: (Contact) -> Unit,
    onWhatsApp: (Contact) -> Unit,
    onOpen: (Contact) -> Unit
) {
    val colors = InfTheme.colors
    InfSheetWindow(
        item = contact,
        title = { it.name },
        onDismiss = onDismiss,
        leading = { ContactAvatar(contact = it, loadPhoto = loadPhoto) }
    ) { current ->
        val actions = buildList {
            add(
                InfAction(
                    icon = if (current.starred) Lucide.StarOff else Lucide.Star,
                    label = stringResource(if (current.starred) R.string.startmenu_unfavourite_contact else R.string.startmenu_favourite_contact),
                    tint = if (current.starred) colors.textPrimary else colors.warning,
                    run = { onToggleFavourite(current) }
                )
            )
            if (current.phone != null) {
                add(InfAction(Lucide.Phone, stringResource(R.string.startmenu_call), colors.textPrimary) { onCall(current) })
            }
            if (current.whatsAppDataId != null) {
                add(InfAction(Lucide.MessageCircle, stringResource(R.string.startmenu_whatsapp), colors.success) { onWhatsApp(current) })
            }
            add(InfAction(Lucide.UserRound, stringResource(R.string.startmenu_open_contact), colors.textPrimary) { onOpen(current) })
        }
        InfActionList(actions = actions, onDismiss = onDismiss)
    }
}
