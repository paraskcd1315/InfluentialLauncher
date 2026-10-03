// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
            content { includeGroupByRegex("com\\.github\\..*") }
        }
    }
}

rootProject.name = "InfluentialLauncher"
include(":app")
include(":designsystem")
include(":windowing")
include(":apps")
include(":devicestatus")
include(":taskbar")
include(":clock")
include(":statusbar")
include(":pins")
include(":startmenu")
include(":calendar")
include(":contacts")
include(":settings")
include(":timetracking")
include(":media")
include(":weather")
include(":glance")
include(":controlcenter")
include(":homescreen")
include(":notifications")
include(":tasks")
include(":shellaccess")
