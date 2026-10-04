// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

plugins {
    alias(libs.plugins.influential.android.library)
    alias(libs.plugins.influential.hilt)
}

android {
    namespace = "com.paraskcd.influentiallauncher.weather"
    defaultConfig {
        buildConfigField("String", "AEMET_API_KEY", "\"${personalSecret("AEMET_API_KEY")}\"")
        buildConfigField("String", "METEOCAT_API_KEY", "\"${personalSecret("METEOCAT_API_KEY")}\"")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.datastore.preferences)
}
