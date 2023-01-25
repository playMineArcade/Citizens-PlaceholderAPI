pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://reposilite.deltapvp.net/releases/") {
            mavenContent {
                releasesOnly()
            }
            content {
                includeGroupByRegex("(?:net.deltapvp.*)|(?:org.minearcade.*)|(?:org.minegem.*)")
            }
        }
        maven("https://reposilite.deltapvp.net/snapshots/") {
            mavenContent {
                snapshotsOnly()
            }
            content {
                includeGroupByRegex("(?:net.deltapvp.*)|(?:org.minearcade.*)|(?:org.minegem.*)")
            }
        }
        maven("https://repo.deltapvp.net/")
        mavenLocal()
    }
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "citizensplaceholderapi"
