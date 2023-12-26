plugins {
    application
    java
    id("com.autonomousapps.dependency-analysis") version "1.28.0"
    id("com.diffplug.spotless") version "6.23.3"
    id("com.github.ben-manes.versions") version "0.50.0"
    id("com.google.protobuf") version "0.9.4"
    id("se.ascp.gradle.gradle-versions-filter") version "0.1.16"
}

sourceSets {
    main {
        java {
            srcDir("build/generated/source/protoc-gen-twirp-java/main/java")
        }
    }
}

dependencies {
    implementation(platform("io.helidon:helidon-bom:2.6.4"))

    implementation("com.google.protobuf:protobuf-java:3.25.1")
    implementation("com.google.protobuf:protobuf-java-util:3.25.1")
    implementation("info.picocli:picocli:4.7.5")
    implementation("io.helidon.common:helidon-common-http")
    implementation("io.helidon.common:helidon-common-reactive")
    implementation("io.helidon.media:helidon-media-common")
    implementation("io.helidon.webclient:helidon-webclient")
    implementation("io.helidon.webserver:helidon-webserver")

    testImplementation(platform("org.junit:junit-bom:5.10.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

repositories {
    mavenCentral()
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.1"
    }
}

application {
    mainClass = "Main"
}

tasks.named<Test>("test") {
    useJUnitPlatform()

    testLogging {
        events("PASSED", "SKIPPED", "FAILED", "STANDARD_OUT", "STANDARD_ERROR")
    }
}
versionsFilter {
    gradleReleaseChannel.set("current")
    checkConstraints.set(true)
    outPutFormatter.set("json")
}

spotless {
    java {
        googleJavaFormat("1.18.1").reflowLongStrings().skipJavadocFormatting()
        formatAnnotations()
        targetExclude("build/**")
    }
}
