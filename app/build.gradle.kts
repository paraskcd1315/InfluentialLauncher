plugins {
    alias(libs.plugins.influential.android.application)
    alias(libs.plugins.influential.android.compose)
    alias(libs.plugins.influential.hilt)
}

android {
    namespace = "com.paraskcd.influentiallauncher"

    defaultConfig {
        applicationId = "com.paraskcd.influentiallauncher"
        versionCode = 2
        versionName = "2.0.0"
        resValue("string", "app_name", "Influential Launcher")
    }

    buildFeatures {
        resValues = true
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
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.hiddenapibypass)
}
