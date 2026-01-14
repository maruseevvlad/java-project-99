import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.plugins.JavaPlugin

val lombokVersion = "1.18.30"
val sentryVersion = "8.19.0"
val jjwtVersion = "0.11.5"
val springdocOpenApiVersion = "2.8.5"
val jsonUnitVersion = "3.2.2"
val instancioVersion = "3.3.0"

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    application
    checkstyle
    jacoco
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.sonarqube") version "4.4.1.3373"
    id("io.sentry.jvm.gradle") version "5.9.0"
}

application {
    mainClass.set("hexlet.code.AppApplication")
}

sonarqube {
    properties {
        property("sonar.gradle.skipCompile", "true")
        property("sonar.sources", "src/main/java")
        property("sonar.tests", "src/test/java")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            layout.buildDirectory
                .file("reports/jacoco/test/jacocoTestReport.xml")
                .get()
                .asFile
                .absolutePath
        )
        property(
            "sonar.coverage.exclusions",
            "**/src/test/java/**/*," +
                    "**/config/**," +
                    "**/dto/**," +
                    "**/model/**," +
                    "**/exception/**," +
                    "**/AppApplication.*"
        )
    }
}

val isCi = System.getenv("CI") == "true"
if (isCi) {
    project.ext.set("sentry.skip", true)
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"
description = "Java Task Manager for Spring Boot"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Compile Only
    compileOnly("org.projectlombok:lombok:$lombokVersion")

    // Annotation Processor
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    // Implementation
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-crypto")
    implementation("io.sentry:sentry-spring-boot-starter-jakarta:$sentryVersion")
    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocOpenApiVersion")

    // Runtime Only
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")

    // Test Implementation
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("net.javacrumbs.json-unit:json-unit-assertj:$jsonUnitVersion")
    testImplementation("org.instancio:instancio-junit:$instancioVersion")

    // Test Runtime Only
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/*Test.class",
                    "**/*Tests.class",
                    "**/config/**",
                    "**/dto/**",
                    "**/model/**",
                    "**/exception/**",
                    "**/AppApplication.*"
                )
            }
        })
    )
}

tasks.build {
    dependsOn(tasks.jacocoTestReport)
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events = setOf(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
        showExceptions = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showCauses = true
        showStackTraces = true
    }
    finalizedBy(tasks.jacocoTestReport)
}

if (isCi) {
    tasks.whenTaskAdded {
        if (name.startsWith("sentry") || name.startsWith("generateSentry")) {
            enabled = false
        }
    }
    tasks.findByName("collectExternalDependenciesForSentry")?.enabled = false
}