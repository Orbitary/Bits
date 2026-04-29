# Bits

💾 A Utility library for Minecraft development.

[![License](https://img.shields.io/github/license/Orbitary/Bits)](LICENSE)

## About

Bits is a multi-platform Minecraft utility library designed to streamline plugin development across Paper and Velocity platforms. It provides common utilities, abstractions, and helper classes to reduce boilerplate code and accelerate your development workflow.

### Features:

- Multi-platform support
- Annotation-based [Brigadier](https://github.com/Mojang/brigadier) Command API (Paper, Velocity, +)
- Automatic Text formatting
- Common Plugin utilities (Color, Sound, Location, Item, Math, Permission APIs)

> [!NOTE]
> This project is in active development, expect regular changes and additions!

## Installation

### Gradle (Kotlin DSL)

```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://repo.bitsquidd.xyz/repository/bit/") }
}

dependencies {
    // Core API (required)
    implementation("xyz.bitsquidd:api:0.0.13")

    // Platform implementation (optional)
    implementation("xyz.bitsquidd:paper:0.0.13")
    implementation("xyz.bitsquidd:velocity:0.0.13")
    implementation("xyz.bitsquidd:fabric:0.0.13")
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
    <groupId>xyz.bitsquidd</groupId>
    <artifactId>api</artifactId>
    <version>0.0.13</version>
</dependency>

<!-- Platform implementation (optional) -->
<dependency>
    <groupId>xyz.bitsquidd</groupId>
    <artifactId>paper/velocity/fabric</artifactId>
    <version>0.0.13</version>
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
