/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

import xyz.bitsquidd.util.includeLibrary
import xyz.bitsquidd.util.relocate
import xyz.bitsquidd.util.shade

/*
 * This file is part of Bits, licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2024-2026 ImBit
 *
 * Enjoy the Bits and Bobs :)
 */

description = "🦑 Utility API for Bits Plugin development."

dependencies {
    shade(rootProject.libs.classgraph)

    implementation(rootProject.libs.joml)
    implementation(rootProject.libs.logger)
    implementation(rootProject.libs.adventure.text.serializer.plain)
    implementation(rootProject.libs.adventure.text.minimessage)

    includeLibrary(rootProject.libs.jackson.core)
    includeLibrary(rootProject.libs.jackson.databind)
    includeLibrary(rootProject.libs.guava)
    includeLibrary(rootProject.libs.adventure)
}

relocate("io.github.classgraph" to "xyz.bitsquidd.lib.classgraph")
