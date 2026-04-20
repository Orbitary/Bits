/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

import xyz.bitsquidd.util.includeLibrary

repositories {
    maven { url = uri("https://libraries.minecraft.net") }
}

dependencies {
    includeLibrary(project(":api"))

    includeLibrary(rootProject.libs.brigadier)

    compileOnly(rootProject.libs.joml)
}

//relocate("com.mojang.brigadier" to "xyz.bitsquidd.lib.brigadier")
