// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

import org.gradle.api.Plugin
import org.gradle.api.Project

class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        pluginManager.apply("com.google.devtools.ksp")
        pluginManager.apply("com.google.dagger.hilt.android")

        dependencies.add("implementation", catalog.library("hilt-android"))
        dependencies.add("ksp", catalog.library("hilt-compiler"))
    }
}
