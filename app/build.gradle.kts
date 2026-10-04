// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

import java.util.Properties

plugins {
    alias(libs.plugins.influential.android.application)
    alias(libs.plugins.influential.android.compose)
    alias(libs.plugins.influential.hilt)
}

val releaseSigning = Properties().apply {
    rootProject.file("keystore.properties").takeIf { it.isFile }?.inputStream()?.use { load(it) }
}
val releaseSigningKeys = listOf("storeFile", "storePassword", "keyAlias", "keyPassword")
val releaseSigningReady = releaseSigningKeys.all { !releaseSigning.getProperty(it).isNullOrBlank() }

android {
    namespace = "com.paraskcd.influentiallauncher"

    signingConfigs {
        if (releaseSigningReady) {
            create("release") {
                storeFile = file(releaseSigning.getProperty("storeFile"))
                storePassword = releaseSigning.getProperty("storePassword")
                keyAlias = releaseSigning.getProperty("keyAlias")
                keyPassword = releaseSigning.getProperty("keyPassword")
            }
        }
    }

    defaultConfig {
        applicationId = "com.paraskcd.influentiallauncher"
        versionCode = 50
        versionName = "2.12.3"
        resValue("string", "app_name", "Influential Launcher")
    }

    buildFeatures {
        resValues = true
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            resValue("string", "app_name", "Influential Dev")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (releaseSigningReady) signingConfig = signingConfigs.getByName("release")
        }
    }
}

dependencies {
    implementation(project(":designsystem"))
    implementation(project(":windowing"))
    implementation(project(":apps"))
    implementation(project(":devicestatus"))
    implementation(project(":taskbar"))
    implementation(project(":statusbar"))
    implementation(project(":pins"))
    implementation(project(":startmenu"))
    implementation(project(":clock"))
    implementation(project(":calendar"))
    implementation(project(":contacts"))
    implementation(project(":settings"))
    implementation(project(":timetracking"))
    implementation(project(":media"))
    implementation(project(":weather"))
    implementation(project(":glance"))
    implementation(project(":controlcenter"))
    implementation(project(":homescreen"))
    implementation(project(":notifications"))
    implementation(project(":tasks"))
    implementation(project(":shellaccess"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.hiddenapibypass)
}
