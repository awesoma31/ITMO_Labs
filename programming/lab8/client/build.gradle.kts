plugins {
    `java-library`
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
}

dependencies {
    api(project(":common"))

    implementation("org.apache.logging.log4j:log4j-api:2.23.1")
    implementation("org.apache.logging.log4j:log4j-core:2.23.1")
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

javafx {
    version = "21.0.3"
    modules("javafx.controls", "javafx.fxml")
}

application {
    mainClass = "org.awesoma.client.App"
    mainModule = "org.awesoma.client"
}

tasks {
    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(
            listOf(
                "compileJava",
                "processResources",
                ":common:jar"
            )
        ) // We need this for Gradle optimization to work
        archiveClassifier.set("fat") // Naming the jar
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) } // Provided we set it up in the application plugin configuration
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) } +
                sourcesMain.output
        from(contents)
    }
    build {
        dependsOn(fatJar) // Trigger fat jar creation during build
    }
}

description = "client"