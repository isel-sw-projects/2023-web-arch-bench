plugins {
    application
}

group = "pt.isel"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.apache.jmeter:ApacheJMeter_bom:5.6.2"))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("us.abstracta.jmeter:jmeter-java-dsl:1.23.3")
    implementation("us.abstracta.jmeter:jmeter-java-dsl-dashboard:1.23.3")
    implementation(project(":config-utils"))
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("pt.isel.MainKt")
}