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

allprojects {
    dependencies {
        paperweight.paperDevBundle(paperLibs.versions.paper.api.get())
    }
}

dependencies {
    api(project(":minecraft"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}