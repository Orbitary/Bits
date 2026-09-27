/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

import xyz.orbitary.util.providedApi
import xyz.orbitary.util.shade


plugins {
    alias(fabricLibs.plugins.fabric.loom)
}

loom {
    splitEnvironmentSourceSets()

    mods {
        create("bits") {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets["client"])
        }
    }
}

providedApi(fabricLibs.fabric.loader)
providedApi(fabricLibs.fabric.api)

repositories {
    maven { url = uri("https://maven.fabricmc.net/") }
}

dependencies {
    group = "xyz.orbitary.bits.fabric"

    minecraft("com.mojang:minecraft:26.2")

    shade(project(":minecraft"), transitive = true)
    shade(project(":minecraft:module:command"), transitive = true)

    api("net.kyori:adventure-platform-fabric:7.1.0")
    api("me.lucko:fabric-permissions-api:0.7.0")
}

tasks {
    processResources {
        filteringCharset = "UTF-8"

        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        from(sourceSets["client"].output)
        relocate("io.github.classgraph", "xyz.orbitary.lib.classgraph")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}