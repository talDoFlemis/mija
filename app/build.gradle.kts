plugins {
    application
    id("org.javacc.javacc") version "3.0.2"
    idea
    id("io.freefair.lombok") version "8.6"
    antlr
    kotlin("plugin.lombok") version "1.9.23"
    kotlin("jvm") version "1.9.23"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testImplementation("org.assertj:assertj-core:3.25.1")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(libs.guava)

    implementation(platform("org.apache.logging.log4j:log4j-bom:2.23.0"))
    implementation("org.apache.logging.log4j:log4j-api")
    runtimeOnly("org.apache.logging.log4j:log4j-core")

    antlr("org.antlr:antlr4:4.7.1")
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1-Beta")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
}

tasks {
    compileJavacc {
        dependsOn(compileKotlin)
        inputDirectory = file("src/main/javacc")
        outputDirectory = file(layout.buildDirectory.dir("generated/javacc"))
    }
}

sourceSets.configureEach {
    val generateGrammarSource = tasks.named(getTaskName("generate", "GrammarSource"))
    java.srcDir(generateGrammarSource.map { files() })
}

sourceSets {
    main {
        java {
            srcDir(file(layout.buildDirectory.dir("generated/javacc")))
        }
        kotlin {
            srcDir("src/main/kotlin")
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