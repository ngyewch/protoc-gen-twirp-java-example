import com.google.protobuf.gradle.id

plugins {
    application
    java
    id("com.autonomousapps.dependency-analysis") version "2.1.1"
    id("com.diffplug.spotless") version "6.25.0"
    id("com.github.ben-manes.versions") version "0.51.0"
    id("com.google.protobuf") version "0.9.4"
    id("se.ascp.gradle.gradle-versions-filter") version "0.1.16"
}

dependencies {
    implementation(platform("io.github.ngyewch.twirp:twirp-bom:0.4.0"))

    implementation("io.github.ngyewch.twirp:twirp-common")
    implementation("io.github.ngyewch.twirp:twirp-apache-client")
    implementation("io.github.ngyewch.twirp:twirp-helidon-client")
    implementation("io.github.ngyewch.twirp:twirp-helidon-server")

    implementation("com.google.protobuf:protobuf-java")
    implementation("info.picocli:picocli:4.7.6")
    implementation("io.helidon.common:helidon-common-http")
    implementation("io.helidon.common:helidon-common-reactive")
    implementation("io.helidon.webserver:helidon-webserver")

    testImplementation(platform("com.fasterxml.jackson:jackson-bom:2.18.0"))
    testImplementation(platform("org.junit:junit-bom:5.11.1"))
    testImplementation("com.fasterxml.jackson.core:jackson-annotations")
    testImplementation("com.fasterxml.jackson.core:jackson-databind")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

protobuf {
    plugins {
        id("twirp-java") {
            artifact = "io.github.ngyewch.twirp:protoc-gen-twirp-java:0.4.0"
        }
    }
    protoc {
        artifact = "com.google.protobuf:protoc:4.28.2"
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("twirp-java") {
                    option("gen-helidon-client=true")
                    option("gen-helidon-server=true")
                    option("gen-apache-client=true")
                }
            }
        }
    }
}

application {
    mainClass = "Main"
}

tasks {
    named<Test>("test") {
        useJUnitPlatform()

        testLogging {
            events("PASSED", "SKIPPED", "FAILED", "STANDARD_OUT", "STANDARD_ERROR")
        }
    }
}

versionsFilter {
    gradleReleaseChannel.set("current")
    checkConstraints.set(true)
    outPutFormatter.set("json")
}

spotless {
    java {
        googleJavaFormat("1.23.0").reflowLongStrings().skipJavadocFormatting()
        formatAnnotations()
        targetExclude("build/**")
    }
}
