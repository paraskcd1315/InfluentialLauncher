// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.shellaccess.infrastructure.handback

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import com.paraskcd.influentiallauncher.shellaccess.infrastructure.transport.BinderContainer
import com.paraskcd.influentiallauncher.shellaccess.infrastructure.transport.ShellProtocol

class ShellHandbackProvider : ContentProvider() {
    override fun onCreate(): Boolean = true

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        if (method != ShellProtocol.HandbackMethod || extras == null) return null
        extras.classLoader = BinderContainer::class.java.classLoader
        val container = extras.getParcelable(ShellProtocol.HandbackExtraBinder, BinderContainer::class.java)
        container?.binder?.let { HelperBinderSink.onBinderReceived(it) }
        return Bundle()
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? = null
    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
}
