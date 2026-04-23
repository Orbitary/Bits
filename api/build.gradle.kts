/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

import xyz.bitsquidd.util.shade

description = "🦑 Utility API for Bits Plugin development."

dependencies {
    shade(rootProject.libs.classgraph)

    compileOnly(rootProject.libs.adventure.text.serializer.plain)

    api(rootProject.libs.joml)
    api(rootProject.libs.adventure.text.minimessage)
    api(rootProject.libs.logger)
    api(rootProject.libs.jackson.core)
    api(rootProject.libs.jackson.databind)
    api(rootProject.libs.guava)
    api(rootProject.libs.adventure)
}

tasks.shadowJar {
    relocate("io.github.classgraph", "xyz.bitsquidd.lib.classgraph")
}
