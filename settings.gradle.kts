pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
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
            url = uri("https://maven.pkg.github.com/vitor-laudares/AtividadeAutAvan")
            credentials {
                username = "vitor-laudares"
                password = "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"
            }
        }
    }
}

rootProject.name = "AtividadeAutAvan"
include(":app")
include(":vitorautavan")