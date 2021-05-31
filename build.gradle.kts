import org.jetbrains.kotlin.cli.js.loadPluginsForTests

val ktorVersion = "1.6.0"
val exposedVersion = "0.31.1"
val hikariCpVersion = "4.0.3"
val ktorFlywayVersion = "1.2.2"
val logbackVersion = "1.2.3"
val assertjVersion = "3.19.0"
val restAssuredVersion = "4.4.0"
val junitVersion = "5.7.2"
val postgresqlVersion = "42.2.2"

plugins {
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.serialization") version "1.5.10"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    application
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")

    implementation("org.postgresql:postgresql:$postgresqlVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("com.zaxxer:HikariCP:$hikariCpVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    testImplementation("org.assertj:assertj-core:$assertjVersion")
    testImplementation("io.rest-assured:rest-assured:$restAssuredVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("io.ktor:ktor-client-cio:$ktorVersion")
}

application {
    mainClass.set("MainKt")
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveBaseName.set("oreluniver-api")
        project.setProperty("mainClassName", "MainKt")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "MainKt"))
        }
    }
}

tasks.create("stage") {
    dependsOn("shadowJar")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
