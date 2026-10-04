// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        pluginManager.apply("com.android.library")

        extensions.configure<LibraryExtension> {
            compileSdk = InfluentialSdk.COMPILE
            buildFeatures {
                buildConfig = true
            }
            defaultConfig {
                minSdk = InfluentialSdk.MIN
                testInstrumentationRunner = InfluentialSdk.TEST_RUNNER
                buildConfigField("boolean", InfluentialEdition.FLAG, isPersonalEdition.toString())
            }
            compileOptions {
                sourceCompatibility = InfluentialSdk.JAVA
                targetCompatibility = InfluentialSdk.JAVA
            }
        }
        configureKotlinAndroid()
    }
}
