package com.paraskcd.influentiallauncher.controlcenter.infrastructure.shell

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import com.paraskcd.influentiallauncher.controlcenter.domain.model.ShellAccess
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import java.lang.reflect.Method
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShizukuShell @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _access = MutableStateFlow(compute())
    val access: StateFlow<ShellAccess> = _access.asStateFlow()
    private var granted = false

    private val newProcess: Method? by lazy {
        runCatching {
            Shizuku::class.java
                .getDeclaredMethod("newProcess", Array<String>::class.java, Array<String>::class.java, String::class.java)
                .apply { isAccessible = true }
        }.onFailure { Log.w(LogTag, "Shizuku process API unavailable", it) }.getOrNull()
    }

    init {
        Shizuku.addBinderReceivedListenerSticky { refresh() }
        Shizuku.addBinderDeadListener { refresh() }
        Shizuku.addRequestPermissionResultListener { _, _ -> refresh() }
    }

    fun refresh() {
        val current = compute()
        _access.value = current
        if (current == ShellAccess.Ready && !granted) {
            granted = true
            scope.launch {
                run(ShellCommands.GrantSecureSettings.format(context.packageName))
                run(ShellCommands.AllowWriteSettings.format(context.packageName))
            }
        }
    }

    fun requestAccess() {
        when (compute()) {
            ShellAccess.NotInstalled -> open(Intent(Intent.ACTION_VIEW, Uri.parse(StoreUrl)))
            ShellAccess.NotRunning -> context.packageManager.getLaunchIntentForPackage(ShizukuPackage)?.let(::open)
            ShellAccess.NeedsPermission -> runCatching { Shizuku.requestPermission(RequestCode) }
                .onFailure { Log.w(LogTag, "Shizuku permission request failed", it) }
            ShellAccess.Ready -> Unit
        }
    }

    suspend fun run(command: String): Boolean = withContext(Dispatchers.IO) {
        if (compute() != ShellAccess.Ready) return@withContext false
        val start = newProcess ?: return@withContext false
        runCatching {
            val process = start.invoke(null, arrayOf(Shell, ShellFlag, command), null, null) as Process
            val error = process.errorStream.bufferedReader().use { it.readText() }
            val code = process.waitFor()
            if (code != 0) Log.w(LogTag, "'$command' exited $code: ${error.take(ErrorPreview)}")
            code == 0
        }.onFailure { Log.w(LogTag, "'$command' failed", it) }.getOrDefault(false)
    }

    private fun compute(): ShellAccess = when {
        !installed() -> ShellAccess.NotInstalled
        !runCatching { Shizuku.pingBinder() }.getOrDefault(false) -> ShellAccess.NotRunning
        runCatching { Shizuku.checkSelfPermission() }.getOrDefault(PackageManager.PERMISSION_DENIED) != PackageManager.PERMISSION_GRANTED ->
            ShellAccess.NeedsPermission
        else -> ShellAccess.Ready
    }

    private fun installed(): Boolean =
        runCatching { context.packageManager.getPackageInfo(ShizukuPackage, 0) }.isSuccess

    private fun open(intent: Intent) {
        runCatching { context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
            .onFailure { Log.w(LogTag, "cannot open Shizuku", it) }
    }

    private companion object {
        const val LogTag = "ShizukuShell"
        const val ShizukuPackage = "moe.shizuku.privileged.api"
        const val StoreUrl = "https://play.google.com/store/apps/details?id=moe.shizuku.privileged.api"
        const val RequestCode = 7_001
        const val Shell = "sh"
        const val ShellFlag = "-c"
        const val ErrorPreview = 200
    }
}
