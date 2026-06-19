<div align="center">

<img width="4320" height="1080" alt="bits_logo" src="https://github.com/user-attachments/assets/fb7e3cc4-1533-40a9-b240-d2be153ea551" />

# Bits

*- Utility library for Orbitary development -*

[![](https://img.shields.io/github/license/Orbitary/Bits?style=flat-square)](LICENSE)
[![](https://img.shields.io/github/actions/workflow/status/Orbitary/Bits/publish.yml?style=flat-square)](https://github.com/Orbitary/Bits/actions/workflows/publish.yml)
[![](https://img.shields.io/badge/dynamic/json?color=1bd96a&label=modrinth&style=flat-square&query=downloads&suffix=%20downloads&url=https%3A%2F%2Fapi.modrinth.com%2Fv2%2Fproject%2F8ZZ61TMj)](https://modrinth.com/mod/orbits)

</div>

* * *

# ✨ Introduction

**Bits** is a multi-platform utility library designed to streamline Minecraft plugin and mod development, as well as Discord bot development.

- Built for **Paper**, **Velocity**, and **Fabric** (client & server).
- Annotation-based [Brigadier](https://github.com/Mojang/brigadier) **Command API**.
- **Sendable lifecycle builders** — Actionbars, Sidebars, Waypoints, and more.
- **Automatic text formatting** and common plugin utilities.
- Color, Sound, Location, Item, Math, and Permission APIs included.

> [!NOTE]
> This project is in active development — expect regular changes and additions!

## 🛠️ Build info

#### Modules

- **api** — Core interfaces and abstractions
- **paper** — Paper platform implementation
    - **paper:sendable** — Actionbars, Sidebars, Waypoints
    - **paper:command** — Brigadier command framework
- **velocity** — Velocity platform implementation
    - **velocity:command** — Brigadier command framework
- **fabric** — Fabric platform implementation (client + server)

## 💻 API

<details open>
<summary>Gradle (Kotlin)</summary>

```kotlin
repositories {
    maven("https://repo.bitsquidd.xyz/repository/bit/")
}

dependencies {
    // Core API (required)
    implementation("xyz.bitsquidd.bits:api:0.0.20")

    // Platform implementations
    implementation("xyz.bitsquidd.bits.paper:paper:0.0.20")
    implementation("xyz.bitsquidd.bits.velocity:velocity:0.0.20")
    implementation("xyz.bitsquidd.bits.fabric:fabric:0.0.20")

    // Optional: Platform-specific modules
    implementation("xyz.bitsquidd.bits.paper:sendable:0.0.20")
    implementation("xyz.bitsquidd.bits.paper:command:0.0.20")
    implementation("xyz.bitsquidd.bits.velocity:command:0.0.20")
}
```

</details>

<details>
<summary>Gradle (Groovy)</summary>

```groovy
repositories {
    maven { url 'https://repo.bitsquidd.xyz/repository/bit/' }
}

dependencies {
    // Core API (required)
    implementation 'xyz.bitsquidd.bits:api:0.0.20'

    // Platform implementations
    implementation 'xyz.bitsquidd.bits.paper:paper:0.0.20'
    implementation 'xyz.bitsquidd.bits.velocity:velocity:0.0.20'
    implementation 'xyz.bitsquidd.bits.fabric:fabric:0.0.20'

    // Optional: Platform-specific modules
    implementation 'xyz.bitsquidd.bits.paper:sendable:0.0.20'
    implementation 'xyz.bitsquidd.bits.paper:command:0.0.20'
    implementation 'xyz.bitsquidd.bits.velocity:command:0.0.20'
}
```

</details>

<details>
<summary>Maven</summary>

```xml

<repositories>
    <repository>
        <id>repo.bitsquidd.xyz</id>
        <url>https://repo.bitsquidd.xyz/repository/bit/</url>
    </repository>
</repositories>

<dependencies>
<!-- Core API (required) -->
<dependency>
    <groupId>xyz.bitsquidd.bits</groupId>
    <artifactId>api</artifactId>
    <version>0.0.20</version>
</dependency>

<!-- Platform implementations -->
<dependency>
    <groupId>xyz.bitsquidd.bits.paper</groupId>
    <artifactId>paper</artifactId>
    <version>0.0.20</version>
</dependency>
<dependency>
    <groupId>xyz.bitsquidd.bits.velocity</groupId>
    <artifactId>velocity</artifactId>
    <version>0.0.20</version>
</dependency>
<dependency>
    <groupId>xyz.bitsquidd.bits.fabric</groupId>
    <artifactId>fabric</artifactId>
    <version>0.0.20</version>
</dependency>
</dependencies>
```

</details>

---
Made with 🦑 by [ImBit](https://github.com/ImBit)
