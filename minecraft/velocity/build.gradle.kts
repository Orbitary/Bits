/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

dependencies {
    api(project(":minecraft"))

    runtimeOnly(rootProject.velocityLibs.velocity.api.get())
    annotationProcessor(rootProject.velocityLibs.velocity.api.get())
}

repositories {
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}