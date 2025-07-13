import org.gradle.model.internal.core.ModelNodes.withType

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    alias(libs.plugins.android.library) apply false
}
val buildToolsVersion by extra("34.0.0")

