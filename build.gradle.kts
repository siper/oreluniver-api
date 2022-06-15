val ktorVersion = "2.0.2"
val exposedVersion = "0.38.2"
val hikariCpVersion = "5.0.1"
val ktorFlywayVersion = "1.2.2"
val logbackVersion = "1.2.11"
val assertjVersion = "3.23.1"
val restAssuredVersion = "5.0.1"
val junitVersion = "5.8.2"
val postgresqlVersion = "42.3.6"

plugins {
    kotlin("jvm") version "1.7.0"
    kotlin("plugin.serialization") version "1.7.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
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
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

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
