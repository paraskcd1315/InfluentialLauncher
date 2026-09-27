package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.contacts.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import com.paraskcd.influentiallauncher.contacts.domain.model.Contact
import com.paraskcd.influentiallauncher.designsystem.theme.InfGlass
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.LetterIndex
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics

@Composable
fun ContactAvatar(
    contact: Contact,
    loadPhoto: suspend (Contact, Int) -> Bitmap?,
    modifier: Modifier = Modifier
) {
    val colors = InfTheme.colors
    val sizePx = with(LocalDensity.current) { StartMenuMetrics.rowIconSize.roundToPx() }
    val photo by produceState<ImageBitmap?>(initialValue = null, contact.id, contact.photoUri, sizePx) {
        value = loadPhoto(contact, sizePx)?.asImageBitmap()
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(StartMenuMetrics.rowIconSize)
            .clip(CircleShape)
            .background(colors.brand.copy(alpha = StartMenuMetrics.avatarAlpha))
            .border(InfGlass.borderWidth, colors.glassBorder, CircleShape)
    ) {
        val bitmap = photo
        if (bitmap != null) {
            Image(bitmap = bitmap, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(StartMenuMetrics.rowIconSize))
        } else {
            Text(
                text = LetterIndex.letterOf(contact.name).toString(),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = colors.brandText
            )
        }
    }
}
