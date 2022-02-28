import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.4"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id ("org.jetbrains.kotlin.plugin.allopen") version "1.6.0"
    id ("org.jetbrains.kotlin.plugin.noarg") version "1.6.0"
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.spring") version "1.6.0"
    kotlin("plugin.jpa") version "1.6.0"
    java
}

noArg {
    annotation("javax.persistence.Entity")
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

allprojects {
    group = "com.gaveship"
    version = "2022.0.0"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "kotlin")
    if (name == "category") {
        apply(plugin = "org.springframework.boot")
        apply(plugin = "org.jetbrains.kotlin.plugin.spring")

        dependencies {
            implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
            implementation("org.jetbrains.kotlin:kotlin-reflect")
            implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
            implementation("io.github.microutils:kotlin-logging")
            testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
        }

        tasks.jar {
            enabled = true
        }
        tasks.forEach {
            if (it.name == "bootJar") {
                it.enabled = false
            }
        }
    }

    dependencyManagement {
        dependencies {
            imports {
                mavenBom("org.springframework.cloud:spring-cloud-dependencies:2021.0.1")
            }
            dependency("io.github.microutils:kotlin-logging:1.12.5")
            dependency("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
            dependency("io.kotest:kotest-runner-junit5:5.1.0")
            dependency("io.kotest:kotest-property:5.1.0")
            dependency("io.kotest:kotest-assertions-core:5.1.0")
            dependency("io.kotest.extensions:kotest-extensions-spring:1.1.0")
            dependency("io.mockk:mockk:1.12.2")
            dependency("com.ninja-squad:springmockk:3.1.1")
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}
