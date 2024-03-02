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
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
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
        sourceDirs.add(file(layout.buildDirectory.dir("generated/org/example/parser")))
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
