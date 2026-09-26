package com.paraskcd.influentiallauncher.contacts.infrastructure

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import androidx.core.net.toUri
import com.paraskcd.influentiallauncher.contacts.domain.model.Contact
import com.paraskcd.influentiallauncher.contacts.domain.ports.ContactsSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactsContractSource @Inject constructor(
    @ApplicationContext private val context: Context
) : ContactsSource {

    override val permission: String = Manifest.permission.READ_CONTACTS

    override val callPermission: String = Manifest.permission.CALL_PHONE

    override fun hasPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    override fun contacts(): Flow<List<Contact>> = callbackFlow {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                trySend(read())
            }
        }
        trySend(read())
        if (hasPermission()) {
            context.contentResolver.registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, observer)
        }
        awaitClose { context.contentResolver.unregisterContentObserver(observer) }
    }.flowOn(Dispatchers.IO)

    override fun open(contact: Contact): Boolean = runCatching {
        val uri = ContactsContract.Contacts.getLookupUri(contact.id, contact.lookupKey)
        context.startActivity(Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }.onFailure { Log.w(LogTag, "open contact failed", it) }.isSuccess

    override suspend fun photo(contact: Contact, sizePx: Int): Bitmap? {
        val uri = contact.photoUri ?: return null
        return withContext(Dispatchers.IO) {
            runCatching {
                context.contentResolver.openInputStream(uri.toUri())?.use(BitmapFactory::decodeStream)?.scale(sizePx, sizePx)
            }.onFailure { Log.w(LogTag, "photo failed", it) }.getOrNull()
        }
    }

    override fun call(contact: Contact): Boolean {
        val number = contact.phone ?: return false
        val canCall = ContextCompat.checkSelfPermission(context, callPermission) == PackageManager.PERMISSION_GRANTED
        val action = if (canCall) Intent.ACTION_CALL else Intent.ACTION_DIAL
        return launch(Intent(action, Uri.fromParts("tel", number, null)), "call failed")
    }

    override fun whatsApp(contact: Contact): Boolean {
        val dataId = contact.whatsAppDataId ?: return false
        val intent = Intent(Intent.ACTION_VIEW)
            .setDataAndType(ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, dataId), WhatsAppChatMime)
            .setPackage(WhatsAppPackage)
        return launch(intent, "whatsapp failed")
    }

    private fun launch(intent: Intent, failure: String): Boolean = runCatching {
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }.onFailure { Log.w(LogTag, failure, it) }.isSuccess

    private fun phones(): Map<Long, String> {
        val primary = mutableMapOf<Long, String>()
        val first = mutableMapOf<Long, String>()
        context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            PhoneProjection,
            null,
            null,
            null
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val contactId = cursor.getLong(0)
                val number = cursor.getString(1)?.takeIf { it.isNotBlank() } ?: continue
                if (cursor.getInt(2) == 1) primary[contactId] = number
                first.putIfAbsent(contactId, number)
            }
        }
        return first + primary
    }

    private fun whatsAppRows(): Map<Long, Long> = buildMap {
        context.contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            DataProjection,
            "${ContactsContract.Data.MIMETYPE} = ?",
            arrayOf(WhatsAppChatMime),
            null
        )?.use { cursor ->
            while (cursor.moveToNext()) putIfAbsent(cursor.getLong(0), cursor.getLong(1))
        }
    }

    private fun read(): List<Contact> {
        if (!hasPermission()) return emptyList()
        return runCatching {
            val phones = phones()
            val whatsApp = whatsAppRows()
            context.contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                Projection,
                "${ContactsContract.Contacts.HAS_PHONE_NUMBER} = 1 OR ${ContactsContract.Contacts.STARRED} = 1",
                null,
                "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} COLLATE NOCASE ASC"
            )?.use { cursor ->
                buildList {
                    while (cursor.moveToNext()) {
                        val name = cursor.getString(2)?.takeIf { it.isNotBlank() } ?: continue
                        val id = cursor.getLong(0)
                        add(
                            Contact(
                                id = id,
                                lookupKey = cursor.getString(1).orEmpty(),
                                name = name,
                                starred = cursor.getInt(3) == 1,
                                photoUri = cursor.getString(4),
                                phone = phones[id],
                                whatsAppDataId = whatsApp[id]
                            )
                        )
                    }
                }
            }.orEmpty()
        }.onFailure { Log.w(LogTag, "reading contacts failed", it) }.getOrDefault(emptyList())
    }

    private companion object {
        const val LogTag = "ContactsSource"
        const val WhatsAppPackage = "com.whatsapp"
        const val WhatsAppChatMime = "vnd.android.cursor.item/vnd.com.whatsapp.profile"
        val PhoneProjection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.IS_SUPER_PRIMARY
        )
        val DataProjection = arrayOf(
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data._ID
        )
        val Projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.STARRED,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
        )
    }
}
