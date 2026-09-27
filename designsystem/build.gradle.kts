plugins {
    alias(libs.plugins.influential.android.library)
    alias(libs.plugins.influential.android.compose)
}

android {
    namespace = "com.paraskcd.influentiallauncher.designsystem"
}

dependencies {
    api(libs.icons.lucide)
    implementation(libs.haze)
    implementation(libs.haze.blur)
}
