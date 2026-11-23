/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

import xyz.bitsquidd.util.providedApi


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

val transitiveInclude: Configuration by configurations.creating {
    exclude(group = "com.mojang")
    exclude(group = "net.fabricmc")
    exclude(group = "net.kyori")
    exclude(group = "io.netty")
    exclude(group = "com.google.guava")
    exclude(group = "org.joml")
    exclude(group = "org.jspecify")
    exclude(group = "org.slf4j")
    exclude(group = "org.jetbrains")
}

providedApi(fabricLibs.fabric.loader)
providedApi(fabricLibs.fabric.api)

repositories {
    maven { url = uri("https://maven.fabricmc.net/") }
}

dependencies {
    group = "xyz.orbitary.bits.fabric"

    minecraft("com.mojang:minecraft:26.3")

    api(project(":minecraft"))
    api(project(":minecraft:module:command"))
    transitiveInclude(project(":minecraft"))
    transitiveInclude(project(":minecraft:module:command"))

    api("net.kyori:adventure-platform-fabric:7.2.0")
    api("me.lucko:fabric-permissions-api:0.7.0")

    testImplementation(platform(rootProject.libs.junit.bom))
    testImplementation(rootProject.libs.junit.jupiter)
    testRuntimeOnly(rootProject.libs.junit.launcher)
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    shadowJar {
        enabled = false
    }

    processResources {
        filteringCharset = "UTF-8"

        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

afterEvaluate {
    dependencies {
        transitiveInclude.resolvedConfiguration.resolvedArtifacts.forEach {
            val id = it.id.componentIdentifier
            if (id is ProjectComponentIdentifier) include(project(id.projectPath))
            else include(it.moduleVersion.id.toString())
        }
    }
}
