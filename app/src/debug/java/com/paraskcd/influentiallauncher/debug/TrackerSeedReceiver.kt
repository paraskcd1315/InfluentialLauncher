package com.paraskcd.influentiallauncher.debug

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.paraskcd.influentiallauncher.timetracking.domain.ports.CredentialsStore
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Stores tracker credentials sent from adb; absent extras keep the stored values. */
class TrackerSeedReceiver : BroadcastReceiver() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface SeedEntryPoint {
        fun credentialsStore(): CredentialsStore
    }

    override fun onReceive(context: Context, intent: Intent) {
        val store = EntryPointAccessors.fromApplication(context.applicationContext, SeedEntryPoint::class.java).credentialsStore()
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                store.update { current ->
                    current.copy(
                        togglToken = intent.getStringExtra(TogglToken) ?: current.togglToken,
                        kimaiUrl = intent.getStringExtra(KimaiUrl) ?: current.kimaiUrl,
                        kimaiToken = intent.getStringExtra(KimaiToken) ?: current.kimaiToken
                    )
                }
                Log.i(LogTag, "tracker credentials seeded")
            } finally {
                pending.finish()
            }
        }
    }

    private companion object {
        const val LogTag = "TrackerSeed"
        const val TogglToken = "toggl_token"
        const val KimaiUrl = "kimai_url"
        const val KimaiToken = "kimai_token"
    }
}
