pluginManagement {
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

rootProject.name = "MusicApp"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":core")
include(":design-system")
include(":features")
include(":features:home")
include(":features:search")
include(":features:favorites")
include(":features:auth")
include(":core:network")
include(":core:database")
include(":core:exoplayer")
include(":features:album")
include(":features:artist")
include(":features:playlist")
include(":features:settings")
