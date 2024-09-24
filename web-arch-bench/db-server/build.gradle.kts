plugins {
    application
}

group = "pt.isel"
version = "unspecified"

application {
    mainClass.set("pt.isel.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-simple:1.6.4")
    implementation("com.h2database:h2:2.1.214")
    implementation("org.apache.commons:commons-dbcp2:2.9.0")
    implementation("com.google.guava:guava:33.2.1-jre")
    testImplementation(kotlin("test"))
    implementation(project(":config-utils"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}