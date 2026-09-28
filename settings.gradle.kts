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
