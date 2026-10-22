/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

description = "💬 Discord slash command adapter for the Bits command model."

dependencies {
    api(project(":api:module:command"))
    api(discordLibs.jda)

    testImplementation(platform(rootProject.libs.junit.bom))
    testImplementation(rootProject.libs.junit.jupiter)
    testRuntimeOnly(rootProject.libs.junit.launcher)
}

tasks.test {
    useJUnitPlatform()
}
