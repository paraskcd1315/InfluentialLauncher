// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

plugins {
    alias(libs.plugins.influential.android.library)
    alias(libs.plugins.influential.hilt)
}

android {
    namespace = "com.paraskcd.influentiallauncher.timetracking"
    defaultConfig {
        buildConfigField("String", "TOGGL_API_TOKEN", "\"${personalSecret("TOGGL_API_TOKEN")}\"")
        buildConfigField("String", "KIMAI_URL", "\"${personalSecret("KIMAI_URL")}\"")
        buildConfigField("String", "KIMAI_TOKEN", "\"${personalSecret("KIMAI_TOKEN")}\"")
    }
}

dependencies {
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.okhttp)
}
