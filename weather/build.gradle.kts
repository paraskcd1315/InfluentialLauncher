// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

import java.util.Properties

plugins {
    alias(libs.plugins.influential.android.library)
    alias(libs.plugins.influential.hilt)
}

val secrets = Properties().apply {
    val file = rootProject.file("secrets.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

android {
    namespace = "com.paraskcd.influentiallauncher.weather"
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        buildConfigField("String", "AEMET_API_KEY", "\"${secrets.getProperty("AEMET_API_KEY", "").trim()}\"")
        buildConfigField("String", "METEOCAT_API_KEY", "\"${secrets.getProperty("METEOCAT_API_KEY", "").trim()}\"")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.datastore.preferences)
}
