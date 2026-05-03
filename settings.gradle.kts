/*
 * This file is part of Bits, licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2024-2026 ImBit
 *
 * Enjoy the Bits and Bobs :)
 */

pluginManagement {
    repositories {
        mavenLocal()

        maven { url = uri("https://repo.bitsquidd.xyz/repository/bit/") }
        maven { url = uri("https://maven.fabricmc.net/") }

        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("fabricLibs") {
            from(files("minecraft/fabric/gradle/libs.versions.toml"))
        }
        create("paperLibs") {
            from(files("minecraft/paper/gradle/libs.versions.toml"))
        }
        create("velocityLibs") {
            from(files("minecraft/velocity/gradle/libs.versions.toml"))
        }
    }
}



rootProject.name = "Bits"

include("api")

include("minecraft")
include("minecraft:paper")
include("minecraft:velocity")
include("minecraft:fabric")

include("minecraft:module")
include("minecraft:module:command")
include("minecraft:module:sendable")