/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

import xyz.bitsquidd.util.shade

/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

plugins {
    alias(paperLibs.plugins.paperweight.userdev)
}

val paperweightPlugin = paperLibs.plugins.paperweight.userdev.get().pluginId
val paperApiVersion = paperLibs.versions.paper.api.get()

allprojects {
    group = "xyz.bitsquidd.bits.paper"

    plugins.apply(paperweightPlugin)

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    }

    repositories {
        maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    }

    dependencies {
        paperweight.paperDevBundle(rootProject.paperLibs.versions.paper.api.get())
    }
}

dependencies {
    api(project(":minecraft"))

    shade(paperLibs.bstats, transitive = true)
}

tasks {
    shadowJar {
        relocate("org.bstats", project.group.toString())
    }
}
