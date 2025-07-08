plugins {
    id("java")
}

// Toolchains:
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

// Dependencies:
repositories {
    mavenCentral()
}

val annotationImplementation: Configuration by configurations.creating {
    configurations.compileOnly.get().extendsFrom(this)
    configurations.testCompileOnly.get().extendsFrom(this)
    configurations.annotationProcessor.get().extendsFrom(this)
    configurations.testAnnotationProcessor.get().extendsFrom(this)
}

dependencies {
    // Project dependencies:
    implementation(project(":AST"))

    // Annotations:
    annotationImplementation("org.projectlombok:lombok:1.18.36")
    implementation("org.jetbrains:annotations:26.0.2")
}
