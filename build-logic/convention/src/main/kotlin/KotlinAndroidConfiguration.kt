// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

fun Project.configureKotlinAndroid() {
    extensions.configure<KotlinAndroidProjectExtension> {
        compilerOptions {
            jvmTarget.set(InfluentialSdk.JVM_TARGET)
        }
    }
    dependencies.add("testImplementation", catalog.library("junit"))
    dependencies.add("testImplementation", catalog.library("kotlinx-coroutines-test"))
}
