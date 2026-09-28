// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.tasks.infrastructure

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Process
import android.util.Log
import com.paraskcd.influentiallauncher.tasks.domain.ports.OpenApps
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShizukuOpenApps @Inject constructor(
    @ApplicationContext private val context: Context
) : OpenApps {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val current = MutableStateFlow<Map<String, Int>>(emptyMap())
    override val taskCounts: StateFlow<Map<String, Int>> = current.asStateFlow()
    private var running: Job? = null

    override fun refresh() {
        if (running?.isActive == true) return
        running = scope.launch { current.value = read() }
    }

    private fun read(): Map<String, Int> {
        if (!ready()) return emptyMap()
        return runCatching {
            HiddenApiBypass.addHiddenApiExemptions(*TasksApi.Exemptions)
            val binder = ShizukuBinderWrapper(SystemServiceHelper.getSystemService(TasksApi.Service))
            val service = Class.forName(TasksApi.StubClass)
                .getMethod(TasksApi.AsInterface, IBinder::class.java)
                .invoke(null, binder)
            val slice = service.javaClass
                .getMethod(TasksApi.GetRecentTasks, Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
                .invoke(service, TasksApi.MaxTasks, TasksApi.IgnoreUnavailable, Process.myUid() / TasksApi.PerUserRange)
            @Suppress("UNCHECKED_CAST")
            val tasks = slice.javaClass.getMethod(TasksApi.GetList).invoke(slice) as List<ActivityManager.RecentTaskInfo>
            tasks.mapNotNull { it.baseIntent.component?.packageName ?: it.baseActivity?.packageName }
                .filter { it != context.packageName }
                .groupingBy { it }
                .eachCount()
        }
            .onFailure { Log.w(LogTag, "recent tasks unavailable", it) }
            .getOrDefault(emptyMap())
    }

    private fun ready(): Boolean = runCatching {
        Shizuku.pingBinder() && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
    }.getOrDefault(false)

    private companion object {
        const val LogTag = "OpenApps"
    }
}
