plugins {
    application
    id("org.javacc.javacc") version "3.0.2"
    idea
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
        inputDirectory = file("src/main/java/org/example/parser")
        outputDirectory = file(layout.buildDirectory.dir("generated/org/example/parser"))
    }
}

sourceSets {
    main {
        java {
            srcDir(layout.buildDirectory.dir("generated/org/example/parser"))
        }
    }
}

idea {
    module {
        generatedSourceDirs.add(file(layout.buildDirectory.dir("generated/org/example/parser")))
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
