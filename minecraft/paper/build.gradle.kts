/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

plugins {
    alias(paperLibs.plugins.paperweight.userdev)
}

repositories {
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
}

val paperweightPlugin = paperLibs.plugins.paperweight.userdev.get().pluginId
val paperApiVersion = paperLibs.versions.paper.api.get()

allprojects {
    plugins.apply(paperweightPlugin)

    dependencies {
        paperweight.paperDevBundle(rootProject.paperLibs.versions.paper.api.get())
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    }
}

dependencies {
    api(project(":minecraft"))
}
