---
name: api-reflection-scanning
description: Guide to ReflectionUtils for classpath scanning and runtime reflection.
---

# Reflection & Classpath Scanning API Guide

`ReflectionUtils` provides **fast, cached reflection** operations and powerful **classpath scanning** for runtime discovery of annotated classes.

## Core Components

### Scanner: Classpath Discovery

Find all classes matching criteria at runtime:

```java
// Find all classes annotated with @Command
Set<Class<?>> commands = ReflectionUtils.Scanner.tryGetAnnotatedClasses(
    "*",  // Package pattern
    Command.class,
    ScannerFlags.DEFAULT
);
```

**Parameters:**
- `packageName`: Package glob pattern ("*" = entire classpath, "com.example.*" = subtree)
- `annotation`: Target annotation class
- `flags`: Scanning options (caching behavior, visibility, etc.)

**Returns:** `Set<Class<?>>` of all classes with the annotation, or empty set on scan error

### Instance: Object Creation

Create instances of classes via reflection:

```java
Object instance = ReflectionUtils.Instance.create(MyClass.class);
```

Throws `ReflectionException` if:
- Class has no no-arg constructor
- Constructor is not accessible
- Constructor throws on invoke

### Method: Method Invocation

Invoke instance and static methods with automatic caching:

```java
Object result = ReflectionUtils.Method.invoke(
    object,
    String.class,  // Return type
    "methodName",
    String.class, Integer.class  // Parameter types
);
```

Methods are cached after first lookup; subsequent calls are fast.

### Primitive: Type Conversions

Convert between primitive and wrapper types:

```java
// Primitive to wrapper
Class<?> intWrapper = ReflectionUtils.Primitive.from(int.class);  // Integer.class

// Wrapper to primitive
Class<?> intPrim = ReflectionUtils.Primitive.to(Integer.class);   // int.class

// Safe conversions (returns self if not applicable)
Class<?> result = ReflectionUtils.Primitive.fromOrSelf(myClass);
```

## Setting the Classloader

For **Minecraft Paper plugins** (or any custom classloader environment):

```java
// In plugin onEnable()
ReflectionUtils.setClassloader(getClass().getClassLoader());

// Or multiple classloaders (e.g., in mod environments)
ReflectionUtils.setClassloader(loader1, loader2, loader3);
```

Must be called **before** any scanning or reflection operations.

## Public API Reference

### Scanner Methods

| Method | Purpose |
|--------|---------|
| `tryGetAnnotatedClasses(pkg, annot, flags)` | Find classes with annotation (returns empty set on error) |
| `getAnnotatedClasses(pkg, annot, flags)` | Find classes (throws if error) |

### Instance Methods

| Method | Purpose |
|--------|---------|
| `create(Class<T>)` | Create instance via no-arg constructor |
| `create(Class<T>, Object...)` | Create instance with arguments |

### Method Methods

| Method | Purpose |
|--------|---------|
| `invoke(obj, returnType, methodName, paramTypes...)` | Invoke instance method (cached) |
| `invokeStatic(class, returnType, methodName, paramTypes...)` | Invoke static method (cached) |

### Primitive Methods

| Method | Purpose |
|--------|---------|
| `from(primitive)` | Convert primitive to wrapper |
| `to(wrapper)` | Convert wrapper to primitive |
| `fromOrSelf(class)` | Convert or return self |
| `toOrSelf(class)` | Convert or return self |

## Use Cases

### Command Discovery (Bits Framework)

```java
// Find all @Command classes at startup
Set<Class<?>> commands = ReflectionUtils.Scanner.tryGetAnnotatedClasses(
    "*",
    Command.class,
    ScannerFlags.DEFAULT
);

// Instantiate each
for (Class<?> clazz : commands) {
    BitsCommand cmd = (BitsCommand)ReflectionUtils.Instance.create(clazz);
    registerCommand(cmd);
}
```

### Serializer Registry (Bits Serialization)

```java
// Find all @Serializer annotated classes
Set<Class<?>> serializers = ReflectionUtils.Scanner.tryGetAnnotatedClasses(
    "*",
    Serializer.class,
    ScannerFlags.DEFAULT
);

// Create and register
for (Class<?> clazz : serializers) {
    MultiSerializer<?> s = (MultiSerializer<?>)ReflectionUtils.Instance.create(clazz);
    registry.register(s.getTargetClass(), s);
}
```

### Dynamic Method Invocation

```java
// Call toString() on any object
String result = ReflectionUtils.Method.invoke(
    myObject,
    String.class,
    "toString"
);
```

## Performance Considerations

- **Method lookups are cached** across the application lifetime
- **Classpath scanning is slow** (done once at startup)
- **Instance creation is not cached** (call `create()` repeatedly as needed)
- For repeated reflection calls, invoke methods multiple times—caching is automatic

## Error Handling

All public methods throw `ReflectionException` on failure:

```java
try {
    ReflectionUtils.Instance.create(MyClass.class);
} catch (ReflectionException e) {
    logger.error("Failed to instantiate MyClass", e);
}
```

Exception wraps underlying causes (NoSuchMethodException, InvocationTargetException, etc.).

## ScannerFlags

Control scanning behavior:
- `DEFAULT`: Standard caching and visibility
- `INCLUDE_ANNOTATIONS`: Include annotation classes themselves
- `EXCLUDE_MODULES`: Ignore Java modules (when applicable)

## Version Info

`@since 0.0.10` - Core reflection API
`@since 0.0.12` - ClassLoader configuration for plugins
