// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.infrastructure

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.util.Log
import com.paraskcd.influentiallauncher.infrastructure.spotlight.SpotlightPeekProtocol

object SearchLauncher {
    private const val LogTag = "SearchLauncher"
    private const val GooglePackage = "com.google.android.googlequicksearchbox"
    private const val GlobalSearch = "android.search.action.GLOBAL_SEARCH"

    fun available(context: Context): Boolean = intentFor(context) != null

    fun open(context: Context): Boolean {
        val intent = intentFor(context) ?: return false
        val options = ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out)
        return runCatching {
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), options.toBundle())
        }.onFailure { Log.w(LogTag, "opening search failed", it) }.isSuccess
    }

    private fun intentFor(context: Context): Intent? {
        val packages = context.packageManager
        val spotlight = SpotlightPeekProtocol.Packages.firstNotNullOfOrNull { packages.getLaunchIntentForPackage(it) }
        if (spotlight != null) return spotlight
        val googleSearch = Intent(GlobalSearch).setPackage(GooglePackage)
        if (googleSearch.resolveActivity(packages) != null) return googleSearch
        return packages.getLaunchIntentForPackage(GooglePackage)
    }
}
