plugins {
    alias(libs.plugins.influential.android.library)
    alias(libs.plugins.influential.android.compose)
    alias(libs.plugins.influential.hilt)
}

android {
    namespace = "com.paraskcd.influentiallauncher.controlcenter"
}

dependencies {
    implementation(project(":designsystem"))
    implementation(project(":windowing"))
    implementation(project(":devicestatus"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.shizuku.api)
    implementation(libs.shizuku.provider)
}
