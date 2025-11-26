plugins {
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
}

// Detect platform for JavaFX native libraries
fun platform(): String {
    val os = System.getProperty("os.name").lowercase()
    if (true) {
        return "win"
    }
    return when {
        os.contains("win") -> "win"
        os.contains("mac") -> "mac"
        else -> "linux"
    }
}

dependencies {
    // JavaFX platform-specific native libs
    implementation("org.openjfx:javafx-controls:22:${platform()}")
    implementation("org.openjfx:javafx-fxml:22:${platform()}")
    implementation("org.openjfx:javafx-graphics:22:${platform()}")

    // Other deps
    implementation("com.google.code.gson:gson:2.11.0")
}

application {
    mainClass.set("org.tts.Launcher")
}

// Build a fat JAR with all JavaFX native libs
tasks.shadowJar {
    archiveBaseName.set("ttsdeckgen")
    archiveClassifier.set("all")
    mergeServiceFiles()
}

// Force JavaFX dependencies to use Windows classifiers
val windowsShadowJar = tasks.register<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJarWindows") {
    archiveBaseName.set("ttsdeckgen")
    archiveClassifier.set("win-all")

    // Add Windows-specific JavaFX native libs
    dependencies {
        include(dependency("org.openjfx:javafx-controls:22:win"))
        include(dependency("org.openjfx:javafx-fxml:22:win"))
        include(dependency("org.openjfx:javafx-graphics:22:win"))
    }

    // Include all other project deps normally
    from(sourceSets.main.get().output)
    configurations = listOf(project.configurations.runtimeClasspath.get())

    mergeServiceFiles()
}
