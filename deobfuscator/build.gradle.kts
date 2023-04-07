plugins {
    id("gradle.plugin.defaults-plugin")
}

dependencies {
    implementation(project(":asm"))
    runtimeOnly(libs.bouncycastle.bcprov)
    runtimeOnly(libs.json)
}