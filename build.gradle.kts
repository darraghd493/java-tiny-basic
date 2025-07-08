plugins {
    id("java")
}

// Toolchains:
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
