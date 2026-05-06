---
name: api-command-framework
description: Guide to using the Bits annotation-based command framework with Brigadier.
---

# Command Framework API Guide

The Bits command framework provides an **annotation-driven, reflection-based** API for registering commands across all Minecraft platforms (Paper, Fabric, Velocity).

## Core Concepts

### Annotation Model

Commands are declared using two key annotations:

- **`@Command`**: Marks a class or method as a command. When applied to a class, creates a command group. When applied to a method, creates an executable command.
  - `value`: Command name
  - `aliases`: Alternative command names
  - `description`: Human-readable description

- **`@Parameter`**: Customizes the argument name for method parameters (optional).

### Example: Simple Command

```java
@Command(value = "give", description = "Give an item to a player", aliases = {"item"})
public class GiveCommand extends BitsCommand {
    @Override
    public void execute(BitsCommandContext<T> context, Player player, ItemStack item) {
        player.getInventory().addItem(item);
        context.sendSuccess("Item given!");
    }
}
```

## Public API Methods

### BitsCommandManager

**Registration & Lifecycle**
- `startup()` - Registers and enables all commands on plugin/mod load
- `getRegisteredCommands()` - Returns the set of all registered commands

**Registries**
- `getArgumentRegistry()` - Returns the parser registry for custom argument types
- `getRequirementRegistry()` - Returns the precondition registry for command checks

**Context Creation**
- `createContext(CommandContext<T>)` - Builds a platform-specific command context
- `createSourceContext(T)` - Wraps a platform source (e.g., CommandSourceStack) for use in commands

**Base Permission**
- `getCommandBasePermission()` - Returns the root permission node (e.g., `bits.command`)

### Automatic Discovery

Commands are discovered from the **entire classpath** at runtime via reflection:
- All classes annotated with `@Command` are found and instantiated
- Command methods are parsed and registered to the Brigadier tree
- If you need to control discovery, override `getAllCommands()` to return a fixed list

## Built-In Argument Types

Supported parameter types in command methods:
- Primitives: `int`, `long`, `float`, `double`, `boolean`, `String`
- Wrappers: `Integer`, `Long`, `Float`, `Double`, `Boolean`
- Minecraft: `UUID`, `Player`, `World`, `Location`, `Vector`, `Block`
- Custom: `Entity`, `ItemStack`, custom enums (via `GenericEnumParser`)

## Common Patterns

### Sub-Commands (Command Groups)

```java
@Command(value = "admin", description = "Admin commands")
public class AdminGroup extends BitsCommand {
    @Command(value = "reload", description = "Reload config")
    public void reload(BitsCommandContext<T> context) {
        // reload logic
    }

    @Command(value = "broadcast", description = "Send broadcast")
    public void broadcast(BitsCommandContext<T> context, String message) {
        // broadcast logic
    }
}
```

### Custom Argument Names

```java
@Command(value = "teleport", description = "Teleport a player")
public class TeleportCommand extends BitsCommand {
    @Override
    public void execute(
        BitsCommandContext<T> context,
        @Parameter("target") Player player,
        @Parameter("destination") Location location
    ) {
        player.teleport(location);
    }
}
```

### Async Execution

```java
@Async
@Command(value = "save", description = "Save async")
public class SaveCommand extends BitsCommand {
    @Override
    public void execute(BitsCommandContext<T> context) {
        // Runs on async thread; avoid player manipulation
    }
}
```

## Requirements & Permissions

Commands can enforce checks via `@Requirement` and `@Permission`:
- Requirement: Custom precondition (e.g., "only on Paper", "sender is console")
- Permission: Minecraft permission string (e.g., `bits.command.admin`)

See platform-specific requirement implementations for available options.

## Extending the Framework

### Adding Custom Argument Parsers

1. Extend `AbstractArgumentParser<T>` for your type
2. Implement `parse(String input)` to convert input to your type
3. Optionally override `getCompletions()` for tab-completion
4. Register in platform's argument registry during initialization

### Adding Custom Requirements

1. Extend `BitsCommandRequirement<T>`
2. Implement `passes(T source)` to evaluate precondition
3. Register in platform's requirement registry

## Key Differences from Raw Brigadier

| Aspect | Bits Framework | Raw Brigadier |
|--------|---|---|
| **Discovery** | Automatic via reflection | Manual registration |
| **Argument Types** | Unified registry across platforms | Per-platform setup |
| **Method Signatures** | Type-safe, idiomatic Java | Generic CommandContext parsing |
| **Permissions** | Built-in via annotations | Manual implementation |
| **Async Support** | `@Async` annotation | Manual thread scheduling |

## Version Info

`@since 0.0.10` - Core command framework
`@since 0.0.13` - Enhanced discovery and context API
