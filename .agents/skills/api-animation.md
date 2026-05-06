---
name: api-animation
description: Guide to the Bits animation system for keyframe-based visual effects.
---

# Animation API Guide

The Bits animation system provides **keyframe-based, easing-driven animations** for Minecraft entities with preset templates and custom composition.

## Core Concepts

### Animation Lifecycle

Animations consist of:
1. **Duration**: Total time in ticks
2. **Keyframes**: Poses at specific time points with easing functions
3. **Loop mode**: How animation repeats (STRAIGHT, PING_PONG, NONE)
4. **Poses**: Transformations applied to entity (translate, rotate, scale)

### Easing Functions

Animations interpolate between keyframes using easing functions (IN_OUT_SIN, LINEAR, etc.).
Easings can be **blended** for custom curves.

## Public API: Animations Factory

Static factory methods in `Animations` class provide pre-built animations:

### Simple Animations

```java
// Floating up and down
Animation floating = Animations.floating();
Animation floating = Animations.floating(40, 0.5f);  // Duration, height

// Continuous spinning
Animation spin = Animations.spin(30);  // Duration in ticks

// Pulsing scale
Animation pulse = Animations.pulse(20, 0.8f, 1.2f);  // Duration, min, max scale

// Sway left-right
Animation swayX = Animations.swayX(100, 5.0f);  // Duration, angle degrees
Animation swayZ = Animations.swayZ(100, 5.0f);  // Sway perpendicular axis

// Wiggle (rapid translation)
Animation wiggleX = Animations.wiggleX(50, 0.2f);  // Duration, amplitude
Animation wiggleZ = Animations.wiggleZ(50, 0.2f);

// Zoom in/out
Animation zoomIn = Animations.zoomIn(30);
Animation zoomOut = Animations.zoomOut(30);
```

### Composite Animations

Combine multiple simple animations:

```java
// Chain animations together
Animation floatSpin = Animations.floatSpin();  // Floating + spinning

// Complex: hot air balloon effect
Animation balloon = Animations.hotAirBalloon();
// = floating(400, 1.0f) + wiggleX(80, 0.15f) + wiggleZ(100, 0.09f)
//   + swayX(90, 2.0f) + swayZ(110, 1.5f) + spin(2000)
```

## Building Custom Animations

Use the builder API for fine-grained control:

```java
Animation custom = Animation.of()
    .duration(50)  // Total ticks
    .loop(AnimationLoopMode.PING_PONG)  // Loop back and forth
    .keyframe(0.00f, pose0, Easings.IN_OUT_SIN)   // Start pose
    .keyframe(0.50f, pose1, Easings.LINEAR)       // Midpoint
    .keyframe(1.00f, pose2, Easings.OUT_BACK)     // End pose
    .build();
```

### Keyframe Structure

- **Time**: 0.0–1.0 (normalized progress)
- **Pose**: Transformations at this point
- **Easing**: How to interpolate to next keyframe

### Poses (AnimationPose)

Built via `AnimationPose.builder()`:

```java
AnimationPose pose = AnimationPose.builder()
    .translateX(0.5f)
    .translateY(1.0f)
    .translateZ(0.2f)
    .rotateX(45f)  // Degrees
    .rotateY(90f)
    .rotateZ(0f)
    .scale(1.2f)   // Uniform scale
    .build();
```

All transformations are optional (defaults to identity).

### Easing Functions

Common easings (from `Easings`):
- Linear: `LINEAR`
- Sine: `IN_SIN`, `OUT_SIN`, `IN_OUT_SIN`
- Back: `IN_BACK`, `OUT_BACK`, `IN_OUT_BACK`
- Quad: `IN_QUAD`, `OUT_QUAD`, `IN_OUT_QUAD`
- And many more...

### Blending Easings

Combine two easings with a weight:

```java
Easing blended = Easings.IN_OUT_SIN.blend(Easings.LINEAR, 0.5f);
// 50% IN_OUT_SIN + 50% LINEAR
```

## Loop Modes

Control animation repetition:

```java
AnimationLoopMode.STRAIGHT  // Play once, then stop
AnimationLoopMode.PING_PONG // Play forward, reverse, repeat
AnimationLoopMode.NONE      // No loop (same as STRAIGHT)
```

## Composing Animations

Chain animations together using `.and()`:

```java
Animation floatingSpinning = Animations.floating(40, 0.5f)
    .and(Animations.spin(30));

Animation complex = Animations.floatSpin()
    .and(Animations.pulse(20, 0.9f, 1.1f));
```

All component animations play **simultaneously** on the same entity.

## Example: Custom Floating Orb

```java
Animation floatingOrb = Animation.of()
    .duration(80)
    .loop(AnimationLoopMode.PING_PONG)
    .keyframe(
        0.00f,
        AnimationPose.builder().translateY(0f).scale(1.0f).build(),
        Easings.IN_OUT_SIN
    )
    .keyframe(
        1.00f,
        AnimationPose.builder().translateY(1.5f).scale(1.1f).build(),
        Easings.IN_OUT_SIN
    )
    .build();
```

## Example: Rotating Cube

```java
Animation rotatingCube = Animation.of()
    .duration(100)
    .loop(AnimationLoopMode.STRAIGHT)  // Play once
    .keyframe(
        0.00f,
        AnimationPose.builder().rotateY(0f).build(),
        Easings.LINEAR
    )
    .keyframe(
        1.00f,
        AnimationPose.builder().rotateY(360f).build(),
        Easings.LINEAR
    )
    .build();
```

## Applying Animations (Platform-Specific)

In Paper, use `AnimationPlayer` to apply animations to entities:

```java
AnimationPlayer player = new AnimationPlayer(entity);
player.play(Animations.floating(40, 0.5f));
```

## Easing Library

For advanced customization, blend easings or use:

```java
EasingHelper.interpolate(easing, progress)  // Get interpolated value [0, 1]
```

## Version Info

`@since 0.0.10` - Core animation framework
`@since 0.0.13` - Enhanced preset animations and composition
