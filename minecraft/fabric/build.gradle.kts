/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

import xyz.bitsquidd.util.shadeLibrary

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

dependencies {
    minecraft("com.mojang:minecraft:26.1.2")

    implementation(rootProject.fabricLibs.fabric.loader)
    implementation(rootProject.fabricLibs.fabric.api)

    implementation("net.kyori:adventure-platform-fabric:6.9.0")
    implementation("me.lucko:fabric-permissions-api:0.5.0")

    shadeLibrary(project(":minecraft"))
    shadeLibrary(project(":api"))
}

tasks {
//    val mergedJar by registering(ShadowJar::class) {
//        archiveClassifier.set("merged")
//        from(sourceSets.main.get().output)
//        from(sourceSets["client"].output)
//        from(zipTree(project(":minecraft").tasks.named<ShadowJar>("shadowJar").flatMap { it.archiveFile }))
//    }
//
//    remapJar {
//        dependsOn(mergedJar)
//        inputFile.set(mergedJar.flatMap { it.archiveFile })
//        archiveClassifier.set("")
//    }

    processResources {
        filteringCharset = "UTF-8"

        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }
}

tasks {
    shadowJar {
        from(sourceSets["client"].output)
    }
}


//afterEvaluate {
//    publishing {
//        publications {
//            named<MavenPublication>("maven") {
//                artifacts.clear()
//                artifact(tasks.named("remapJar"))
//                artifact(tasks.named("sourcesJar"))
//                artifact(tasks.named("javadocJar"))
//            }
//        }
//    }
//}