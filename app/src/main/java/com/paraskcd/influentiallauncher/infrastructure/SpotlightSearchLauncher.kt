package com.paraskcd.influentiallauncher.infrastructure

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.util.Log

object SpotlightSearchLauncher {
    private const val LogTag = "SpotlightSearch"
    private val Packages = listOf("com.paraskcd.spotlightsearch", "com.paraskcd.spotlightsearch.dev")

    fun open(context: Context): Boolean {
        val intent = Packages.firstNotNullOfOrNull { context.packageManager.getLaunchIntentForPackage(it) } ?: return false
        val options = ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out)
        return runCatching {
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), options.toBundle())
        }.onFailure { Log.w(LogTag, "opening Spotlight Search failed", it) }.isSuccess
    }
}
