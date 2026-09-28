// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

object InfluentialSdk {
    const val COMPILE = 37
    const val MIN = 34
    const val TARGET = 36
    val JAVA = JavaVersion.VERSION_21
    val JVM_TARGET = JvmTarget.JVM_21
    const val TEST_RUNNER = "androidx.test.runner.AndroidJUnitRunner"
}
