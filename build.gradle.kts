import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    kotlin("plugin.serialization")version "1.8.21"
    application
    jacoco
    id("io.gitlab.arturbosch.detekt") version "1.18.0-RC3"
    id("org.jetbrains.dokka") version "1.4.32"
}

group = "edu.udo.cs.sopra"
version = "1.0"

repositories {
    mavenCentral()
    maven {
        url = uri("https://sopra-gitlab.cs.tu-dortmund.de/api/v4/projects/1599/packages/maven")
        credentials(HttpHeaderCredentials::class) {
            name = "Private-Token"
            value = "glpat-2e8xQHhP1LxmdUSsNoxK"
        }
        authentication {
            create<HttpHeaderAuthentication>("header")
        }
    }
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

application {
    mainClass.set("MainKt")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
    testImplementation(kotlin("test-junit5"))
    //implementation(group = "tools.aqua", name = "bgw-gui", version = "0.9")
    implementation(group = "tools.aqua", name = "bgw-gui", version = "0.9-4-06a99c3-SNAPSHOT")
    implementation(group = "tools.aqua", name = "bgw-net-common", version = "0.9")
    implementation(group = "tools.aqua", name = "bgw-net-client", version = "0.9")
    implementation(group = "edu.udo.cs.sopra", name = "ntf", version = "1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}

tasks.distZip {
    archiveFileName.set("distribution.zip")
    destinationDirectory.set(layout.projectDirectory.dir("public"))

    into("") {
        from(".") {
            include("HowToPlay.pdf")
        }
    }
}

tasks.test {
    useJUnitPlatform()
    reports.html.outputLocation.set(layout.projectDirectory.dir("public/test"))
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
    ignoreFailures = true
}

tasks.clean {
    delete.add("public")
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(layout.projectDirectory.dir("public/coverage"))
    }

    classDirectories.setFrom(files(classDirectories.files.map {
        fileTree(it) {
            exclude(listOf("view/**", "entity/**", "service/ai/**", "Main*.*"))
        }
    }))
}

detekt {
    // Version of Detekt that will be used. When unspecified the latest detekt
    // version found will be used. Override to stay on the same version.
    toolVersion = "1.18.0-RC3"

    //source.setFrom()
    config = files("detektConfig.yml")

    reports {
        // Enable/Disable HTML report (default: true)
        html {
            enabled = true
            reportsDir = file("public/detekt")
        }

        sarif {
            enabled = false
        }
    }
}

tasks.dokkaHtml.configure {
    outputDirectory.set(projectDir.resolve("public/dokka"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
