// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

plugins {
    alias(libs.plugins.influential.android.library)
    alias(libs.plugins.influential.android.compose)
}

android {
    namespace = "com.paraskcd.influentiallauncher.designsystem"
}

dependencies {
    api(libs.icons.lucide)
}
