// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.tasks.infrastructure

object TasksApi {
    const val Service = "activity_task"
    const val StubClass = "android.app.IActivityTaskManager\$Stub"
    const val AsInterface = "asInterface"
    const val GetRecentTasks = "getRecentTasks"
    const val GetList = "getList"
    const val RemoveTask = "removeTask"
    const val ActivityService = "activity"
    const val ActivityStubClass = "android.app.IActivityManager\$Stub"
    const val ForceStopPackage = "forceStopPackage"
    const val GetRunningAppProcesses = "getRunningAppProcesses"
    const val MaxTasks = 64
    const val IgnoreUnavailable = 0x0002
    const val PerUserRange = 100_000
    val Exemptions = arrayOf("Landroid/app/IActivityTaskManager", "Landroid/app/IActivityManager", "Landroid/content/pm/ParceledListSlice")
}
