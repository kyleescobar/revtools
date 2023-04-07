plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

val catalogs = extensions.getByType<VersionCatalogsExtension>()

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin:1.8.20")
}