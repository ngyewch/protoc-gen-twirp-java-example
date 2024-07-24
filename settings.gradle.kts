val devMode = false

if (devMode) {
    dependencyResolutionManagement {
        repositoriesMode = RepositoriesMode.PREFER_SETTINGS;
        repositories {
            maven {
                url = uri("../protoc-gen-twirp-java/java/build/repo")
            }
            mavenCentral()
        }
    }
}
