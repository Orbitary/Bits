/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

import xyz.bitsquidd.util.providedApi

allprojects {
    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    repositories {
        maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    }
}

providedApi(velocityLibs.velocity.api)

dependencies {
    api(project(":minecraft"))

    annotationProcessor(velocityLibs.velocity.api)
}
