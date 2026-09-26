package com.paraskcd.influentiallauncher.contacts.infrastructure

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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

    private fun read(): List<Contact> {
        if (!hasPermission()) return emptyList()
        return runCatching {
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
                        add(
                            Contact(
                                id = cursor.getLong(0),
                                lookupKey = cursor.getString(1).orEmpty(),
                                name = name,
                                starred = cursor.getInt(3) == 1,
                                photoUri = cursor.getString(4)
                            )
                        )
                    }
                }
            }.orEmpty()
        }.onFailure { Log.w(LogTag, "reading contacts failed", it) }.getOrDefault(emptyList())
    }

    private companion object {
        const val LogTag = "ContactsSource"
        val Projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.STARRED,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
        )
    }
}
