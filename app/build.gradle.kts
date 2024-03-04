plugins {
    application
    id("org.javacc.javacc") version "3.0.2"
    idea
    id("io.freefair.lombok") version "8.6"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(libs.guava)
}

tasks {
    compileJavacc {
        inputDirectory = file("src/main/javacc")
        outputDirectory = file(layout.buildDirectory.dir("generated/javacc"))
    }
}

sourceSets {
    main {
        java {
            srcDir(file(layout.buildDirectory.dir("generated/javacc")))
        }
    }
}

idea {
    module {
        sourceDirs.addAll(files(layout.buildDirectory.dir("generated/javacc")))
        generatedSourceDirs.addAll(files(layout.buildDirectory.dir("generated/javacc")))
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "org.example.App"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
