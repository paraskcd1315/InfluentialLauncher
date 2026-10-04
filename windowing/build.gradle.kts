// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

plugins {
    alias(libs.plugins.influential.android.library)
    alias(libs.plugins.influential.android.compose)
}

android {
    namespace = "com.paraskcd.influentiallauncher.windowing"
}

dependencies {
    implementation(project(":designsystem"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.activity.compose)
}
