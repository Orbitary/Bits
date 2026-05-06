---
name: api-paper-locations
description: Guide to Bits location abstractions and spatial regions in Paper.
---

# Paper Locations & Regions API Guide

The Bits location system provides **type-safe wrappers** around Bukkit locations, block coordinates, and **spatial region abstractions** for efficient area-based queries and operations.

## Core Concepts

### Location Wrappers

Bits provides immutable wrappers for different coordinate systems:

- **`BlockPos`**: Double-precision block coordinates (x, y, z as double)
- **`BlockLoc`**: Integer block coordinates (x, y, z as int)
- **`Locatable`**: Interface for any object with a position
- **`ImmutableLocation`**: Immutable Bukkit Location wrapper
- **`YawAndPitch`**: Rotation (yaw, pitch in degrees)

All are **immutable** and designed for safe passing between methods.

## Public API: Locations Utility

Static methods in `Locations` for common operations:

### Distance Checks

```java
// Safe distance check (handles null and different worlds)
boolean close = Locations.isWithinDistance(loc1, loc2, 50.0);
```

### Collection Operations

Calculate points from collections of `Locatable` objects:

```java
Collection<Locatable> points = /* ... */;

// Center point
BlockPos center = Locations.getMidpoint(points);

// Bounding box corners
BlockPos min = Locations.getMinLocation(points);
BlockPos max = Locations.getMaxLocation(points);
```

Throws `IllegalArgumentException` if collection is empty.

## Regions: Spatial Containment

**`Region`** is an abstract base for spatial areas (cuboid, cylinder, ellipsoid, etc.).

### Core Methods

```java
// Check if location/position is inside
boolean inside = region.contains(Location loc);
boolean inside = region.contains(BlockPos pos);

// Get all blocks whose centers are in the region
Set<BlockLoc> blocks = region.getBlocks();

// Get boundary corners
BlockPos min = region.min();
BlockPos max = region.max();

// Get all block data in region
Set<BlockData> data = region.getBlockData();

// Find blocks matching predicate
Set<BlockLoc> filtered = region.findBlocks(blockData -> blockData.getMaterial().isBlock());

// Async block retrieval
CompletableFuture<Set<BlockLoc>> futureBlocks = region.getBlocksAsync();
```

### Region Implementations

**`CuboidRegion`**: Axis-aligned box

```java
Region cuboid = new CuboidRegion(world, minX, minY, minZ, maxX, maxY, maxZ);
```

**`CylinderRegion`**: Vertical cylinder (XZ plane)

```java
Region cylinder = new CylinderRegion(world, centerX, centerZ, radius, minY, maxY);
```

**`EllipsoidRegion`**: Elliptical sphere

```java
Region ellipsoid = new EllipsoidRegion(
    world,
    centerX, centerY, centerZ,
    radiusX, radiusY, radiusZ
);
```

## Areas: Dynamic Spatial Events

**`Area`** extends region with **event-driven entry/exit detection** as entities move.

### Core Methods

```java
// Check if entity is in area
boolean inside = area.contains(Location loc);

// Manually dispatch enter/exit events
area.onEntityEnter(entity);
area.onEntityExit(entity);

// Get current entities in area
Set<Entity> entities = area.getEntitiesInArea();
```

### Area Implementations

**`TickingArea`**: Polls entity positions on each tick

```java
TickingArea area = new TickingArea(world, cuboidRegion);
area.register();  // Start ticking
area.unregister();  // Stop ticking
```

Automatically dispatches `AreaEnterEvent` / `AreaExitEvent` to listeners.

### Listening to Area Events

```java
Bukkit.getPluginManager().registerEvents(new Listener() {
    @EventHandler
    public void onAreaEnter(AreaEnterEvent event) {
        Entity entity = event.getEntity();
        Area area = event.getArea();
        // Handle entry
    }

    @EventHandler
    public void onAreaExit(AreaExitEvent event) {
        Entity entity = event.getEntity();
        Area area = event.getArea();
        // Handle exit
    }
}, plugin);
```

## Location Rounding

Fine-tune coordinate precision for physics or collision detection:

```java
// Functional interface for rounding strategies
LocationRounding rounding = (coordinate, precision) -> {
    // Custom rounding logic
    return Math.round(coordinate / precision) * precision;
};

// Pre-built: round to nearest block
LocationRounding toBlock = BlockPos::of;
```

Similarly for **`RotationRounding`**:

```java
RotationRounding yawRounding = (yaw, precision) -> {
    return Math.round(yaw / precision) * precision;
};
```

## Location Wrappers: Details

### BlockPos

Double-precision coordinates:

```java
BlockPos pos = BlockPos.of(1.5, 2.0, 3.7);
double x = pos.x;
double y = pos.y;
double z = pos.z;
```

### BlockLoc

Integer coordinates:

```java
BlockLoc block = BlockLoc.of(1, 2, 3);
int x = block.x;
int y = block.y;
int z = block.z;
```

### Locatable Interface

Any object implementing `Locatable`:

```java
public interface Locatable {
    Vector3d asVector();  // Get position as vector
}

// Example: wrap an entity
Locatable entityPos = () -> entity.getLocation().toVector().toVector3d();
```

### ImmutableLocation

Immutable Bukkit Location:

```java
ImmutableLocation imLoc = ImmutableLocation.of(bukkitLocation);
```

### YawAndPitch

Rotation wrapper:

```java
YawAndPitch rotation = YawAndPitch.of(45f, -15f);  // Yaw, Pitch
float yaw = rotation.yaw;
float pitch = rotation.pitch;
```

### CardinalDirections

```java
// Get cardinal direction from location
BlockCardinal direction = BlockCardinal.fromLocation(location);
// NORTH, SOUTH, EAST, WEST, etc.
```

## Example: Zone Protection

```java
// Define a protected zone
CuboidRegion protectedZone = new CuboidRegion(
    Bukkit.getWorld("spawn"),
    -100, 50, -100,  // Min
    100, 150, 100    // Max
);

// Prevent block breaking
if (protectedZone.contains(event.getBlock().getLocation())) {
    event.setCancelled(true);
}
```

## Example: Entity Tracking

```java
// Create ticking area with event notification
TickingArea eventZone = new TickingArea(
    world,
    new CylinderRegion(world, centerX, centerZ, 50, 0, 256)
);

eventZone.register();

// Listen for entries
Bukkit.getPluginManager().registerEvents(new Listener() {
    @EventHandler
    public void onEnter(AreaEnterEvent e) {
        if (e.getEntity() instanceof Player p) {
            p.sendMessage("You entered the zone!");
        }
    }
}, plugin);
```

## Version Info

`@since 0.0.11` - Core location utilities
`@since 0.0.13` - Regions, Areas, and spatial queries
