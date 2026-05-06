---
name: api-text-decorators
description: Guide to the Bits text and decorator system for formatted messages.
---

# Text & Decorator API Guide

The Bits text system provides a **fluent, decorator-based API** for building rich Adventure text components with locale-aware formatting.

## Core Concepts

### Text Class

`Text` is a wrapper around Adventure's `Component` that:
- Applies multiple decorators in a defined sequence
- Supports appending and composition
- Builds audience-specific components (locale-aware)
- Sends messages via Adventure's `Audience` API

### Decorator Model

**`ITextDecorator`**: Transforms a `Component` into a modified version
- Applied in order: pre-defaults → user decorators → post-defaults
- Stateless and idempotent
- Examples: color, click actions, hover text, custom formatters

## Public API Methods

### Text Creation

```java
// From Adventure Component
Text.of(Component component)

// From plain string
Text.of(String plainText)
```

### Decoration

```java
// Apply single decorator
text.decorate(ITextDecorator decorator)

// Apply multiple decorators
text.decorate(ITextDecorator... decorators)
text.decorate(List<ITextDecorator> decorators)
```

Returns a **new** `Text` instance (immutable chain).

### Composition

```java
// Append another Text
text.append(Text other)  // Returns this for fluent chaining
```

### Output

```java
// Send to audience(s)
text.send(Audience audience)

// Get final component for manual sending
text.toComponent()  // Uses default empty locale

// Get component for specific audience (locale-aware)
text.getComponent(Audience audience)
```

## Built-In Decorators

Common decorators available in `minecraft/sendable/text/decorator/`:

- **`ColorFormatter`**: Applies color (red, blue, etc.)
- **`ClickFormatter`**: Adds click action (RUN_COMMAND, OPEN_URL)
- **`HoverFormatter`**: Adds hover text
- **`StyleFormatter`**: Applies font, bold, italic, strikethrough, underline
- **`DynamicColorFormatter`**: Applies gradients or conditional colors

## Example Workflows

### Simple Colored Message

```java
Text message = Text.of("Hello, world!")
    .decorate(new ColorFormatter(Colors.GREEN));

message.send(audience);
```

### Message with Click Action

```java
Text clickable = Text.of("Click me!")
    .decorate(
        new ColorFormatter(Colors.BLUE),
        new ClickFormatter(ClickEvent.Action.SUGGEST_COMMAND, "/help")
    );

clickable.send(audience);
```

### Composed Message with Appends

```java
Text header = Text.of("Important: ")
    .decorate(new ColorFormatter(Colors.RED));

Text body = Text.of("Check your mail!")
    .decorate(new ColorFormatter(Colors.WHITE));

header.append(body).send(audience);
// Sends: "Important: Check your mail!" (red + white)
```

### Locale-Aware Formatting

```java
Text localized = Text.of(Component.translatable("chat.type.text"))
    .decorate(customFormatter);

// Component is built with audience's locale
localized.send(audience);
```

## Decorator Design

### Creating Custom Decorators

Implement `ITextDecorator`:

```java
public class MyDecorator implements ITextDecorator {
    @Override
    public Component format(Component component, Locale locale) {
        // Transform component based on locale
        return component.color(NamedTextColor.GREEN);
    }
}
```

Then use:
```java
text.decorate(new MyDecorator())
```

### Order of Application

Decorators are applied in this order:
1. Pre-default decorators (internal; currently empty)
2. User-provided decorators (in the order specified)
3. Post-default decorators (e.g., `BlankDecorator` for cleanup)

This ensures consistent formatting and cleanup.

## Integration with Sendable

`Text` implements `Sendable` interface, allowing it to be passed to any method accepting sendable objects:

```java
// Sendable is a common interface for messages
Sendable message = Text.of("Hello").decorate(...);
message.send(audience);
```

## Fluent API Pattern

All decoration and composition methods return `Text` (or `this`), enabling chains:

```java
Text.of("Check status")
    .decorate(new ColorFormatter(Colors.YELLOW))
    .decorate(new ClickFormatter(ClickEvent.Action.RUN_COMMAND, "/status"))
    .append(Text.of(" [CLICK]").decorate(new ColorFormatter(Colors.GRAY)))
    .send(audience);
```

## Audience Integration

Messages are sent to **any** Adventure `Audience`:
- Players (single)
- Player collections
- Nested audiences
- Custom audience implementations

Each audience can have a different locale; decorators adapt automatically.