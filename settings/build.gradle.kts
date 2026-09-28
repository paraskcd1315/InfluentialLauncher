// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

plugins {
    alias(libs.plugins.influential.android.library)
    alias(libs.plugins.influential.hilt)
}

android {
    namespace = "com.paraskcd.influentiallauncher.settings"
}

dependencies {
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
}
