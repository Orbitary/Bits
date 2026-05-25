<img width="4320" height="1080" alt="bits_logo" src="https://github.com/user-attachments/assets/fb7e3cc4-1533-40a9-b240-d2be153ea551" />
<p align="center"><b> 💾 A Utility library for Orbitary development.</b></p>


[![License](https://img.shields.io/github/license/Orbitary/Bits)](LICENSE)
[![Java CI](https://github.com/Orbitary/Bits/actions/workflows/publish.yml/badge.svg)](https://github.com/Orbitary/Bits/actions/workflows/publish.yml)
[![Modrinth Downloads](https://img.shields.io/badge/dynamic/json?color=1bd96a&label=Modrinth&query=downloads&suffix=%20downloads&url=https%3A%2F%2Fapi.modrinth.com%2Fv2%2Fproject%2F8ZZ61TMj)](https://modrinth.com/mod/orbits)

## About

Bits is a multi-platform utility library designed to streamline:

- Minecraft plugin development across Paper and Velocity platforms.
- Minecraft mod development using Fabric (both client and server).
- Discord Bot Development
- And more!

### Features:

- Multi-platform support
- Annotation-based [Brigadier](https://github.com/Mojang/brigadier) Command API
- Sendable lifecycle builders (Actionbars, Sidebars, Waypoints, +)
- Automatic Text formatting
- Common Plugin utilities (Color, Sound, Location, Item, Math, Permission APIs)

> [!NOTE]
> This project is in active development, expect regular changes and additions!

## Installation

### Gradle (Kotlin DSL)

```kotlin
repositories {
    maven { url = uri("https://repo.bitsquidd.xyz/repository/bit/") }
}

dependencies {
    // Core API
    implementation("xyz.bitsquidd.bits:api:0.0.17")

    // Platform implementation
    implementation("xyz.bitsquidd.bits.paper:paper:0.0.17")
    implementation("xyz.bitsquidd.bits.velocity:velocity:0.0.17")
    implementation("xyz.bitsquidd.bits.fabric:fabric:0.0.17")
    
    // Optional: Platform-specific modules
    implementation("xyz.bitsquidd.bits.paper:sendable:0.0.17")
    implementation("xyz.bitsquidd.bits.paper:command:0.0.17")
    implementation("xyz.bitsquidd.bits.velocity:command:0.0.17")
}
```

### Maven

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
    <version>0.0.17</version>
</dependency>

<!-- Platform implementation (optional) -->
<dependency>
    <groupId>xyz.bitsquidd.bits.paper</groupId>
    <artifactId>paper</artifactId>
    <version>0.0.17</version>
</dependency>
<dependency>
    <groupId>xyz.bitsquidd.bits.velocity</groupId>
    <artifactId>velocity</artifactId>
    <version>0.0.17</version>
</dependency>
<dependency>
    <groupId>xyz.bitsquidd.bits.fabric</groupId>
    <artifactId>fabric</artifactId>
    <version>0.0.17</version>
</dependency>
</dependencies>
```

## Libraries

- **API** - Core interfaces and abstractions
- **Paper** - Paper platform implementation
- **Velocity** - Velocity platform implementation
- **Fabric** - Fabric platform implementation
    - **Client** - Fabric client implementation
    - **Server** - Fabric server implementation

---
Made with 🦑 by [ImBit](https://github.com/ImBit)
