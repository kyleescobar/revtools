plugins {
    id("gradle.plugin.defaults-plugin")
    application
}

dependencies {
    implementation(project(":asm"))
    implementation(project(":deobfuscator"))
    implementation(libs.clikt)
    implementation(libs.jansi)
}

application {
    mainClass.set("io.github.kyleescobar.revtools.RevTools")
}

tasks.named<JavaExec>("run") {
    workingDir = rootProject.projectDir
}