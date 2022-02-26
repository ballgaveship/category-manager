version = "2022.0.0-SNAPSHOT"

plugins {
    id("com.google.cloud.tools.jib") version "3.2.0"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("com.h2database:h2")
}

jib {
    from {
        image = "amazoncorretto:17"
        platforms {
            platform {
                architecture = "arm64"
                os = "linux"
            }
        }
    }
    to {
        image = "ballgaveship/category-manager"
        tags = setOf("${project.version}", "latest")
    }
    container {
        ports = listOf("8080")
        creationTime = "USE_CURRENT_TIMESTAMP"
        jvmFlags = listOf(
            "-XX:+UseContainerSupport",
            "-XX:+UseStringDeduplication",
            "-XX:+OptimizeStringConcat"
        )
    }
}