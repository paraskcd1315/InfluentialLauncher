// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

plugins {
    alias(libs.plugins.influential.android.library)
    alias(libs.plugins.influential.android.compose)
    alias(libs.plugins.influential.hilt)
}

android {
    namespace = "com.paraskcd.influentiallauncher.taskbar"
}

dependencies {
    implementation(project(":designsystem"))
    implementation(project(":windowing"))
    implementation(project(":apps"))
    implementation(project(":pins"))
    implementation(project(":homescreen"))
    implementation(project(":notifications"))
    implementation(project(":tasks"))
    implementation(project(":settings"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.coroutines.android)
}
