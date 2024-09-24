pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "web-arch-bench"
include("lib", "spring-mvc","benchmark","spring-webflux","db-server","config-utils")