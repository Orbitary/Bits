/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

repositories {
    maven { url = uri("https://libraries.minecraft.net") }
}

dependencies {
    api(project(":api:module:command"))
    api(rootProject.libs.brigadier)

    testImplementation(platform(rootProject.libs.junit.bom))
    testImplementation(rootProject.libs.junit.jupiter)
    testRuntimeOnly(rootProject.libs.junit.launcher)
}

tasks.test {
    useJUnitPlatform()
}
