package gradle.plugin

plugins {
    kotlin("jvm")
}

val catalogs = extensions.getByType<VersionCatalogsExtension>()
val libs = catalogs.named("libs")

group = "io.github.kyleescobar.revtools"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(libs.findBundle("tinylog").get())
}