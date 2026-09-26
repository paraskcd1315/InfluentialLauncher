package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.contacts.sheets

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MessageCircle
import com.composables.icons.lucide.Phone
import com.composables.icons.lucide.Pin
import com.composables.icons.lucide.PinOff
import com.composables.icons.lucide.UserRound
import com.paraskcd.influentiallauncher.contacts.domain.model.Contact
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.shared.sheets.SheetAction
import com.paraskcd.influentiallauncher.startmenu.presentation.shared.sheets.SheetActions
import com.paraskcd.influentiallauncher.startmenu.presentation.shared.sheets.SheetWindow
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.contacts.components.ContactAvatar

@Composable
fun ContactMenuSheet(
    contact: Contact?,
    pinned: Boolean,
    loadPhoto: suspend (Contact, Int) -> Bitmap?,
    onDismiss: () -> Unit,
    onTogglePin: (Contact) -> Unit,
    onCall: (Contact) -> Unit,
    onWhatsApp: (Contact) -> Unit,
    onOpen: (Contact) -> Unit
) {
    val colors = InfTheme.colors
    SheetWindow(
        item = contact,
        title = { it.name },
        onDismiss = onDismiss,
        leading = { ContactAvatar(contact = it, loadPhoto = loadPhoto) }
    ) { current ->
        val actions = buildList {
            add(
                SheetAction(
                    icon = if (pinned) Lucide.PinOff else Lucide.Pin,
                    label = stringResource(if (pinned) R.string.startmenu_unpin_contact else R.string.startmenu_pin_contact),
                    tint = colors.textPrimary,
                    run = { onTogglePin(current) }
                )
            )
            if (current.phone != null) {
                add(SheetAction(Lucide.Phone, stringResource(R.string.startmenu_call), colors.textPrimary) { onCall(current) })
            }
            if (current.whatsAppDataId != null) {
                add(SheetAction(Lucide.MessageCircle, stringResource(R.string.startmenu_whatsapp), colors.success) { onWhatsApp(current) })
            }
            add(SheetAction(Lucide.UserRound, stringResource(R.string.startmenu_open_contact), colors.textPrimary) { onOpen(current) })
        }
        SheetActions(actions = actions, onDismiss = onDismiss)
    }
}
