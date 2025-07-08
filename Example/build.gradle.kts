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
dependencies {
    // Project dependencies:
    implementation(project(":AST"))
    implementation(project(":Parser"))
}
