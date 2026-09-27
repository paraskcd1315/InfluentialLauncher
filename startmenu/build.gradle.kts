plugins {
    alias(libs.plugins.influential.android.library)
    alias(libs.plugins.influential.android.compose)
    alias(libs.plugins.influential.hilt)
}

android {
    namespace = "com.paraskcd.influentiallauncher.startmenu"
}

dependencies {
    implementation(project(":designsystem"))
    implementation(project(":windowing"))
    implementation(project(":apps"))
    implementation(project(":pins"))
    implementation(project(":calendar"))
    implementation(project(":contacts"))
    implementation(project(":settings"))
    implementation(project(":timetracking"))
    implementation(project(":homescreen"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.coroutines.android)
}
