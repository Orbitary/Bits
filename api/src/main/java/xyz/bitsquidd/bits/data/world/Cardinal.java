/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.data.world;

/**
 * Represents the six cardinal directions in a 3D space.
 * <p>
 * <b>IMPORTANT NOTE:</b> this API is intended for use in a Minecraft world, which has unique rotation conventions:
 * North = <b>-</b>Z, South = <b>+</b>Z
 * See <a href="https://minecraft.wiki/w/Coordinates">Minecraft Wiki: Coordinates</a> for more info
 *
 * @since 0.0.11
 */
public enum Cardinal {
    NORTH(new int[]{0, 0, -1}),
    EAST(new int[]{1, 0, 0}),
    SOUTH(new int[]{0, 0, 1}),
    WEST(new int[]{-1, 0, 0}),
    UP(new int[]{0, 1, 0}),
    DOWN(new int[]{0, -1, 0}),
    ;

    public final int[] unitVector;

    Cardinal(int[] unitVector) {
        this.unitVector = unitVector;
    }

    //region Static Constructors
    /**
     * Resolves horizontal-plane {@code yaw} (in degrees) to a {@link Cardinal}.
     * Only returns {@link #NORTH}, {@link #EAST}, {@link #SOUTH}, or {@link #WEST} — pitch is not considered.
     */
    public static Cardinal fromYaw(float yaw) {
        float normalizedYaw = ((yaw % 360) + 360) % 360;

        if (normalizedYaw >= 315 || normalizedYaw < 45) {
            return NORTH;
        } else if (normalizedYaw < 135) {
            return EAST;
        } else if (normalizedYaw < 225) {
            return SOUTH;
        } else {
            return WEST;
        }
    }
    //endregion

    //region Flipping
    public Cardinal flip() {
        return switch (this) {
            case NORTH -> SOUTH;
            case EAST -> WEST;
            case SOUTH -> NORTH;
            case WEST -> EAST;
            case UP -> DOWN;
            case DOWN -> UP;
        };
    }

    public Cardinal flip(Axis axis) {
        return switch (axis) {
            case X -> switch (this) {
                case NORTH, SOUTH, UP, DOWN -> this.flip();
                default -> this; // EAST and WEST are unaffected by flipping across the X axis
            };
            case Y -> switch (this) {
                case EAST, WEST, UP, DOWN -> this.flip();
                default -> this; // NORTH and SOUTH are unaffected by flipping across the Y axis
            };
            case Z -> switch (this) {
                case NORTH, SOUTH, EAST, WEST -> this.flip();
                default -> this; // UP and DOWN are unaffected by flipping across the Z axis
            };
        };
    }
    //endregion

    //region Rotations
    public Cardinal rotate(Axis axis, WholeRot rot) {
        if (rot == WholeRot.R0) return this; // no rotation

        return switch (axis) {
            case X -> switch (this) {
                case NORTH, SOUTH, UP, DOWN -> this.rotateX(rot);
                default -> this; // EAST and WEST are unaffected by rotation around the X axis
            };
            case Y -> switch (this) {
                case EAST, WEST, UP, DOWN -> this.rotateY(rot);
                default -> this; // NORTH and SOUTH are unaffected by rotation around the Y axis
            };
            case Z -> switch (this) {
                case NORTH, SOUTH, EAST, WEST -> this.rotateZ(rot);
                default -> this; // UP and DOWN are unaffected by rotation around the Z axis
            };
        };
    }

    public Cardinal rotateX(WholeRot rot) {
        return switch (rot) {
            case R0 -> this;
            case R90 -> switch (this) {
                case NORTH -> DOWN;
                case SOUTH -> UP;
                case UP -> NORTH;
                case DOWN -> SOUTH;
                default -> this; // EAST and WEST are unaffected by rotation around the X axis
            };
            case R180 -> switch (this) {
                case NORTH -> SOUTH;
                case SOUTH -> NORTH;
                case UP -> DOWN;
                case DOWN -> UP;
                default -> this; // EAST and WEST are unaffected by rotation around the X axis
            };
            case R270 -> switch (this) {
                case NORTH -> UP;
                case SOUTH -> DOWN;
                case UP -> SOUTH;
                case DOWN -> NORTH;
                default -> this; // EAST and WEST are unaffected by rotation around the X axis
            };
        };
    }

    public Cardinal rotateY(WholeRot rot) {
        return switch (rot) {
            case R0 -> this;
            case R90 -> switch (this) {
                case NORTH -> EAST;
                case EAST -> SOUTH;
                case SOUTH -> WEST;
                case WEST -> NORTH;
                default -> this; // UP and DOWN are unaffected by rotation around the Y axis
            };
            case R180 -> switch (this) {
                case NORTH -> SOUTH;
                case EAST -> WEST;
                case SOUTH -> NORTH;
                case WEST -> EAST;
                default -> this; // UP and DOWN are unaffected by rotation around the Y axis
            };
            case R270 -> switch (this) {
                case NORTH -> WEST;
                case EAST -> NORTH;
                case SOUTH -> EAST;
                case WEST -> SOUTH;
                default -> this; // UP and DOWN are unaffected by rotation around the Y axis
            };
        };
    }

    public Cardinal rotateZ(WholeRot rot) {
        return switch (rot) {
            case R0 -> this;
            case R90 -> switch (this) {
                case NORTH -> EAST;
                case EAST -> NORTH;
                case SOUTH -> WEST;
                case WEST -> SOUTH;
                default -> this; // UP and DOWN are unaffected by rotation around the Z axis
            };
            case R180 -> switch (this) {
                case NORTH -> SOUTH;
                case EAST -> WEST;
                case SOUTH -> NORTH;
                case WEST -> EAST;
                default -> this; // UP and DOWN are unaffected by rotation around the Z axis
            };
            case R270 -> switch (this) {
                case NORTH -> WEST;
                case EAST -> SOUTH;
                case SOUTH -> EAST;
                case WEST -> NORTH;
                default -> this; // UP and DOWN are unaffected by rotation around the Z axis
            };
        };
    }
    //endregion

}
