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
    api(project(":api"))

    api(rootProject.libs.brigadier)
}

