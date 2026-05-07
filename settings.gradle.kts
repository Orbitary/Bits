/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
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
include("minecraft:module")
include("minecraft:module:command")
include("minecraft:module:sendable")

include("minecraft:paper")
include("minecraft:paper:module")
include("minecraft:paper:module:command")
include("minecraft:paper:module:sendable")

include("minecraft:velocity")
include("minecraft:velocity:module")
include("minecraft:velocity:module:command")


include("minecraft:fabric")



