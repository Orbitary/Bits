---
name: api-serialization
description: Guide to Bits serialization system using Jackson and custom serializers.
---

# Serialization API Guide

The Bits serialization system provides **automatic JSON serialization/deserialization** via Jackson with custom type support for Adventure components, JOML math types, and Bukkit objects.

## Core Concepts

### SerializationManager

Central singleton that manages Jackson `ObjectMapper` configuration and custom serializers:

```java
// Access the pre-configured mapper
ObjectMapper mapper = SerializationManager.SERIALIZER;
```

The mapper is **auto-configured** with all registered `@Serializer` implementations at startup.

### MultiSerializer Interface

Custom types implement `MultiSerializer<T>` to define bidirectional serialization:

```java
public interface MultiSerializer<T> {
    Class<T> getTargetClass();
    
    // Return Jackson serializer
    JsonSerializer<T> jacksonSerializer();
    
    // Return Jackson deserializer
    JsonDeserializer<T> jacksonDeserializer();
}
```

Annotate with `@Serializer` for auto-discovery:

```java
@Serializer
public class MyTypeSerializer implements MultiSerializer<MyType> {
    // ...
}
```

## Public API: ObjectMapper

Via `SerializationManager.SERIALIZER`, use standard Jackson methods:

### Serialization (Object → JSON)

```java
// Object to JSON string
String json = SerializationManager.SERIALIZER.writeValueAsString(myObject);

// Object to JsonNode tree
JsonNode tree = SerializationManager.SERIALIZER.valueToTree(myObject);

// Object to JSON file
SerializationManager.SERIALIZER.writeValue(new File("data.json"), myObject);
```

### Deserialization (JSON → Object)

```java
// JSON string to object
MyType obj = SerializationManager.SERIALIZER.readValue(json, MyType.class);

// JsonNode to object
MyType obj = SerializationManager.SERIALIZER.treeToValue(tree, MyType.class);

// JSON file to object
MyType obj = SerializationManager.SERIALIZER.readValue(new File("data.json"), MyType.class);
```

### JSON Parsing

```java
// Parse JSON string to tree
JsonNode tree = SerializationManager.SERIALIZER.readTree(json);

// Parse JSON from file
JsonNode tree = SerializationManager.SERIALIZER.readTree(new File("data.json"));
```

### Generic Types (TypeReference)

```java
// Deserialize List<MyType>
List<MyType> list = SerializationManager.SERIALIZER.readValue(
    json,
    new TypeReference<List<MyType>>() {}
);

// Deserialize Map<String, MyType>
Map<String, MyType> map = SerializationManager.SERIALIZER.readValue(
    json,
    new TypeReference<Map<String, MyType>>() {}
);
```

## Built-In Serializers

Pre-registered types that serialize out-of-the-box:

### Bukkit/Paper Types
- `Location`
- `Vector`
- `World`

### Adventure Text Components
- `Component` (net.kyori.adventure.text.Component)
- `Key` (net.kyori.adventure.key.Key)

### JOML Math
- `Vector3f`, `Vector2i`
- `Quaternionf`

### Time & UUID
- `UUID`
- `Duration`, `Instant`

### Example: Serializing Adventure Component

```java
Component msg = Component.text("Hello").color(NamedTextColor.BLUE);

// Serialize
String json = SerializationManager.SERIALIZER.writeValueAsString(msg);
// Result: {"text":"Hello","color":"blue"}

// Deserialize
Component restored = SerializationManager.SERIALIZER.readValue(json, Component.class);
```

## Creating Custom Serializers

### Step 1: Implement MultiSerializer<T>

```java
public class MyTypeSerializer implements MultiSerializer<MyType> {
    @Override
    public Class<MyType> getTargetClass() {
        return MyType.class;
    }

    @Override
    public JsonSerializer<MyType> jacksonSerializer() {
        return (value, gen, serializers) -> {
            gen.writeStartObject();
            gen.writeStringField("name", value.name());
            gen.writeNumberField("id", value.id());
            gen.writeEndObject();
        };
    }

    @Override
    public JsonDeserializer<MyType> jacksonDeserializer() {
        return new StdDeserializer<MyType>(MyType.class) {
            @Override
            public MyType deserialize(JsonParser p, DeserializationContext ctx)
                    throws IOException {
                JsonNode node = p.getCodec().readTree(p);
                String name = node.get("name").asText();
                int id = node.get("id").asInt();
                return new MyType(name, id);
            }
        };
    }
}
```

### Step 2: Annotate with @Serializer

```java
@Serializer
public class MyTypeSerializer implements MultiSerializer<MyType> {
    // ...
}
```

### Step 3: Auto-Discovery

Place the class in the classpath. It will be discovered and registered at startup via reflection.

## Error Handling

Jackson is configured to **fail on unknown properties** and missing creator properties:

```java
try {
    MyType obj = SerializationManager.SERIALIZER.readValue(json, MyType.class);
} catch (JsonMappingException e) {
    // Handle unknown properties, missing fields, etc.
}
```

## Nullability Awareness

The mapper respects `@Nullable` annotations via `NullableAwareIntrospector`:
- Fields marked `@Nullable` can deserialize as null
- Others must be non-null; missing values throw an error

## Configuration Details

The mapper is built with:
- `FAIL_ON_MISSING_CREATOR_PROPERTIES`: Required fields must be present in JSON
- `FAIL_ON_UNKNOWN_PROPERTIES`: Unknown JSON keys cause error
- `FAIL_ON_NULL_FOR_PRIMITIVES`: Primitives cannot be null
- `DEFAULT_VIEW_INCLUSION`: All fields included by default
- `NullableAwareIntrospector`: Respects `@Nullable` annotations

## Common Patterns

### Serializing Collections

```java
// List<MyType>
String json = SerializationManager.SERIALIZER.writeValueAsString(list);

// Deserialize with type info
List<MyType> restored = SerializationManager.SERIALIZER.readValue(
    json,
    new TypeReference<List<MyType>>() {}
);
```

### Storing Config Objects

```java
// Save to file
Config config = new Config(/* ... */);
SerializationManager.SERIALIZER.writeValue(new File("config.json"), config);

// Load from file
Config restored = SerializationManager.SERIALIZER.readValue(
    new File("config.json"),
    Config.class
);
```

### Handling Polymorphic Types

Register all subtypes or use `@JsonTypeInfo` annotation for polymorphism.

## Version Info

`@since 0.0.10` - Core serialization API
`@since 0.0.13` - Enhanced adventure component support
