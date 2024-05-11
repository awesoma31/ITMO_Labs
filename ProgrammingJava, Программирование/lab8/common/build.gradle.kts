plugins {
    id("java")
}

group = "org.awesoma"
version = "unspecified"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation("commons-cli:commons-cli:1.7.0")
    implementation("io.github.cdimascio:dotenv-java:3.0.0")
}
