// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.tasks.infrastructure

import android.app.ActivityManager
import android.content.Context
import android.os.IBinder
import android.os.Process
import android.util.Log
import com.paraskcd.influentiallauncher.shellaccess.domain.model.ShellState
import com.paraskcd.influentiallauncher.shellaccess.domain.ports.ShellAccess
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HelperOpenApps @Inject constructor(
    @ApplicationContext private val context: Context,
    private val shell: ShellAccess
) : OpenApps {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val current = MutableStateFlow<Map<String, Int>>(emptyMap())
    override val taskCounts: StateFlow<Map<String, Int>> = current.asStateFlow()
    private var running: Job? = null

    override fun refresh() {
        if (running?.isActive == true) return
        running = scope.launch { current.value = read() }
    }

    override fun close(packageName: String) {
        scope.launch {
            if (!ready()) return@launch
            runCatching {
                val tasks = taskService() ?: return@launch
                recentTasks(tasks).filter { packageOf(it) == packageName }.forEach {
                    tasks.javaClass.getMethod(TasksApi.RemoveTask, Int::class.javaPrimitiveType).invoke(tasks, it.taskId)
                }
                val activities = service(TasksApi.ActivityService, TasksApi.ActivityStubClass) ?: return@launch
                activities.javaClass
                    .getMethod(TasksApi.ForceStopPackage, String::class.java, Int::class.javaPrimitiveType)
                    .invoke(activities, packageName, userId())
            }.onFailure { Log.w(LogTag, "close failed", it) }
            current.value = read()
        }
    }

    private fun read(): Map<String, Int> {
        if (!ready()) return emptyMap()
        return runCatching {
            val tasks = taskService() ?: return emptyMap()
            recentTasks(tasks)
                .mapNotNull(::packageOf)
                .filter { it != context.packageName }
                .groupingBy { it }
                .eachCount()
        }
            .onFailure { Log.w(LogTag, "recent tasks unavailable", it) }
            .getOrDefault(emptyMap())
    }

    private fun taskService(): Any? = service(TasksApi.Service, TasksApi.StubClass)

    private fun service(name: String, stubClass: String): Any? {
        HiddenApiBypass.addHiddenApiExemptions(*TasksApi.Exemptions)
        val binder: IBinder = shell.systemService(name) ?: return null
        return Class.forName(stubClass).getMethod(TasksApi.AsInterface, IBinder::class.java).invoke(null, binder)
    }

    private fun recentTasks(service: Any): List<ActivityManager.RecentTaskInfo> {
        val slice = service.javaClass
            .getMethod(TasksApi.GetRecentTasks, Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
            .invoke(service, TasksApi.MaxTasks, TasksApi.IgnoreUnavailable, userId())
        @Suppress("UNCHECKED_CAST")
        return slice.javaClass.getMethod(TasksApi.GetList).invoke(slice) as List<ActivityManager.RecentTaskInfo>
    }

    private fun packageOf(task: ActivityManager.RecentTaskInfo): String? =
        task.baseIntent.component?.packageName ?: task.baseActivity?.packageName

    private fun userId(): Int = Process.myUid() / TasksApi.PerUserRange

    private fun ready(): Boolean = shell.state.value is ShellState.Ready

    private companion object {
        const val LogTag = "OpenApps"
    }
}
