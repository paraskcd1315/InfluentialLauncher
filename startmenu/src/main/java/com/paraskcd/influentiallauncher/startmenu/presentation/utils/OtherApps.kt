// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.model.OtherApp

object OtherApps {
    val all = listOf(
        OtherApp("com.paraskcd.spotlightsearch", R.string.startmenu_other_spotlight, R.string.startmenu_other_spotlight_caption),
        OtherApp("org.tvtracking.app", R.string.startmenu_other_tvtracking, R.string.startmenu_other_tvtracking_caption)
    )

    fun open(context: Context, app: OtherApp): Boolean {
        val launch = context.packageManager.getLaunchIntentForPackage(app.packageName)
        val intents = listOfNotNull(
            launch,
            Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${app.packageName}")),
            Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${app.packageName}"))
        )
        return intents.any { intent ->
            try {
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                true
            } catch (_: ActivityNotFoundException) {
                false
            }
        }
    }

    fun openLink(context: Context, url: String): Boolean = try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        true
    } catch (_: ActivityNotFoundException) {
        false
    }
}
