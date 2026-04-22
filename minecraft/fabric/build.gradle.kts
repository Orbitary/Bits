/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

import xyz.bitsquidd.util.includeLibrary

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

repositories {
    mavenLocal()

    maven { url = uri("https://maven.fabricmc.net/") }

    mavenCentral()
}

allprojects {
    dependencies {
        compileOnly(rootProject.fabricLibs.fabric.loader)
        compileOnly(rootProject.fabricLibs.fabric.api)
    }
}

dependencies {
    minecraft("com.mojang:minecraft:26.1.2")

    api(project(":minecraft"))

    api("net.kyori:adventure-platform-fabric:6.9.0")
    api("me.lucko:fabric-permissions-api:0.5.0")
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
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}