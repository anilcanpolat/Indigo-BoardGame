import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    application
    jacoco
    id("io.gitlab.arturbosch.detekt") version "1.18.0-RC3"
    id("org.jetbrains.dokka") version "1.4.32"
}

group = "edu.udo.cs.sopra"
version = "1.0"

repositories {
    mavenCentral()
}

application {
    mainClass.set("MainKt")
}

dependencies {
    testImplementation(kotlin("test-junit5"))
    implementation(group = "tools.aqua", name = "bgw-gui", version = "0.8.1")
    implementation(group = "tools.aqua", name = "bgw-net-common", version = "0.8.1")
    implementation(group = "tools.aqua", name = "bgw-net-client", version = "0.8.1")
}

tasks.distZip {
    archiveFileName.set("distribution.zip")
    destinationDirectory.set(layout.projectDirectory.dir("public"))
}

tasks.test {
    useJUnitPlatform()
    reports.html.outputLocation.set(layout.projectDirectory.dir("public/test"))
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
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
