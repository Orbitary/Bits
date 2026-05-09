/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

import xyz.bitsquidd.util.providedApi
import xyz.bitsquidd.util.shade

allprojects {
    group = "xyz.bitsquidd.bits.velocity"

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

    shade(velocityLibs.bstats, transitive = true)
}

tasks {
    shadowJar {
        relocate("org.bstats", project.group.toString())
    }
}