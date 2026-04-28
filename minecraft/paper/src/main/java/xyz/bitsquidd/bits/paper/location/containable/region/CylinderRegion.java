/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.location.containable.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import xyz.bitsquidd.bits.data.world.Axis;
import xyz.bitsquidd.bits.paper.location.containable.area.visualisation.Center;
import xyz.bitsquidd.bits.paper.location.containable.area.visualisation.Corner;
import xyz.bitsquidd.bits.paper.location.containable.area.visualisation.Edge;
import xyz.bitsquidd.bits.paper.location.containable.area.visualisation.impl.RegionVisualiser;
import xyz.bitsquidd.bits.paper.location.wrapper.BlockPos;
import xyz.bitsquidd.bits.paper.location.wrapper.Locatable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class CylinderRegion extends Region {
    private final BlockPos centerBottom;
    private final Axis axis;
    private final double radius;
    private final double height;

    public CylinderRegion(World world, BlockPos centerBottom, Axis axis, double radius, double height) {
        super(world);
        if (radius <= 0) throw new IllegalArgumentException("Radius must be positive");
        if (height <= 0) throw new IllegalArgumentException("Height must be positive");
        this.centerBottom = centerBottom;
        this.axis = axis;
        this.radius = radius;
        this.height = height;
    }

    public CylinderRegion(Location centerBottom, Axis axis, double radius, double height) {
        this(centerBottom.getWorld(), BlockPos.of(centerBottom), axis, radius, height);
    }

    //region Java Object Overrides
    @Override
    public String toString() {
        return "CylinderRegion{centerBottom=" + centerBottom + ", axis=" + axis + ", radius=" + radius + ", height=" + height + "}";
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, centerBottom, axis, radius, height);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CylinderRegion other)) return false;
        return Objects.equals(world, other.world) &&
          Objects.equals(centerBottom, other.centerBottom) &&
          axis == other.axis &&
          Double.compare(radius, other.radius) == 0 &&
          Double.compare(height, other.height) == 0;
    }
    //endregion

    /**
     * For Axis.Y: circle is on X/Z, height runs up Y.
     * For Axis.X: circle is on Y/Z, height runs along X.
     * For Axis.Z: circle is on X/Y, height runs along Z.
     */
    @Override
    public boolean contains(Locatable locatable) {
        if (locatable == null) return false;

        Vector v = locatable.asVector();
        double cx = centerBottom.x;
        double cy = centerBottom.y;
        double cz = centerBottom.z;

        return switch (axis) {
            case Y -> {
                double axialOffset = v.getY() - cy;
                double dx = v.getX() - cx;
                double dz = v.getZ() - cz;
                yield axialOffset >= 0 && axialOffset <= height
                  && (dx * dx + dz * dz) <= (radius * radius);
            }
            case X -> {
                double axialOffset = v.getX() - cx;
                double dy = v.getY() - cy;
                double dz = v.getZ() - cz;
                yield axialOffset >= 0 && axialOffset <= height
                  && (dy * dy + dz * dz) <= (radius * radius);
            }
            case Z -> {
                double axialOffset = v.getZ() - cz;
                double dx = v.getX() - cx;
                double dy = v.getY() - cy;
                yield axialOffset >= 0 && axialOffset <= height
                  && (dx * dx + dy * dy) <= (radius * radius);
            }
        };
    }

    @Override
    public BlockPos center() {
        return switch (axis) {
            case Y -> BlockPos.of(centerBottom.x, centerBottom.y + height / 2, centerBottom.z);
            case X -> BlockPos.of(centerBottom.x + height / 2, centerBottom.y, centerBottom.z);
            case Z -> BlockPos.of(centerBottom.x, centerBottom.y, centerBottom.z + height / 2);
        };
    }

    @Override
    public BlockPos min() {
        return switch (axis) {
            case Y -> BlockPos.of(centerBottom.x - radius, centerBottom.y, centerBottom.z - radius);
            case X -> BlockPos.of(centerBottom.x, centerBottom.y - radius, centerBottom.z - radius);
            case Z -> BlockPos.of(centerBottom.x - radius, centerBottom.y - radius, centerBottom.z);
        };
    }

    @Override
    public BlockPos max() {
        return switch (axis) {
            case Y -> BlockPos.of(centerBottom.x + radius, centerBottom.y + height, centerBottom.z + radius);
            case X -> BlockPos.of(centerBottom.x + height, centerBottom.y + radius, centerBottom.z + radius);
            case Z -> BlockPos.of(centerBottom.x + radius, centerBottom.y + radius, centerBottom.z + height);
        };
    }

    @Override
    public CylinderRegion expand(double x, double y, double z) {
        // TODO independent radial and axial expansion
        return switch (axis) {
            case Y -> {
                double radialExpand = Math.max(x, z);
                yield new CylinderRegion(
                  world,
                  BlockPos.of(centerBottom.x, centerBottom.y - y, centerBottom.z),
                  axis,
                  radius + radialExpand,
                  height + y * 2
                );
            }
            case X -> {
                double radialExpand = Math.max(y, z);
                yield new CylinderRegion(
                  world,
                  BlockPos.of(centerBottom.x - x, centerBottom.y, centerBottom.z),
                  axis,
                  radius + radialExpand,
                  height + x * 2
                );
            }
            case Z -> {
                double radialExpand = Math.max(x, y);
                yield new CylinderRegion(
                  world,
                  BlockPos.of(centerBottom.x, centerBottom.y, centerBottom.z - z),
                  axis,
                  radius + radialExpand,
                  height + z * 2
                );
            }
        };
    }

    @Override
    public CylinderRegion shift(double x, double y, double z) {
        return new CylinderRegion(
          world,
          BlockPos.of(centerBottom.x + x, centerBottom.y + y, centerBottom.z + z),
          axis, radius, height
        );
    }


    public BlockPos getCenterBottom() {
        return centerBottom;
    }

    public Axis getAxis() {
        return axis;
    }

    public double getRadius() {
        return radius;
    }

    public double getHeight() {
        return height;
    }


    @Override
    protected Set<RegionVisualiser> createVisualiser() {
        Set<RegionVisualiser> visualisers = new HashSet<>();

        int edges = 8;
        BlockPos c = center();

        switch (axis) {
            case Y -> {
                // Arcs on the XZ plane at bottom and top Y
                visualisers.add(Edge.arc(BlockPos.of(c.x, min().y, c.z), radius, 0, 360, 0, 0));
                visualisers.add(Edge.arc(BlockPos.of(c.x, max().y, c.z), radius, 0, 360, 0, 0));

                // Vertical (Y-axis) edges around the circumference
                for (int i = 0; i < edges; i++) {
                    double angle = 2 * Math.PI * i / edges;
                    double x = c.x + radius * Math.cos(angle);
                    double z = c.z + radius * Math.sin(angle);
                    visualisers.add(Edge.straight(
                      BlockPos.of(x, min().y, z),
                      BlockPos.of(x, max().y, z)
                    ));
                }

                visualisers.add(Corner.of(BlockPos.of(c.x, min().y, c.z)));
                visualisers.add(Corner.of(BlockPos.of(c.x, max().y, c.z)));
            }
            case X -> {
                visualisers.add(Edge.arc(BlockPos.of(min().x, c.y, c.z), radius, 0, 360, 90, 0));
                visualisers.add(Edge.arc(BlockPos.of(max().x, c.y, c.z), radius, 0, 360, 90, 0));

                for (int i = 0; i < edges; i++) {
                    double angle = 2 * Math.PI * i / edges;
                    double y = c.y + radius * Math.cos(angle);
                    double z = c.z + radius * Math.sin(angle);
                    visualisers.add(Edge.straight(
                      BlockPos.of(min().x, y, z),
                      BlockPos.of(max().x, y, z)
                    ));
                }

                visualisers.add(Corner.of(BlockPos.of(min().x, c.y, c.z)));
                visualisers.add(Corner.of(BlockPos.of(max().x, c.y, c.z)));
            }
            case Z -> {
                visualisers.add(Edge.arc(BlockPos.of(c.x, c.y, min().z), radius, 0, 360, 0, 90));
                visualisers.add(Edge.arc(BlockPos.of(c.x, c.y, max().z), radius, 0, 360, 0, 90));

                for (int i = 0; i < edges; i++) {
                    double angle = 2 * Math.PI * i / edges;
                    double x = c.x + radius * Math.cos(angle);
                    double y = c.y + radius * Math.sin(angle);
                    visualisers.add(Edge.straight(
                      BlockPos.of(x, y, min().z),
                      BlockPos.of(x, y, max().z)
                    ));
                }

                visualisers.add(Corner.of(BlockPos.of(c.x, c.y, min().z)));
                visualisers.add(Corner.of(BlockPos.of(c.x, c.y, max().z)));
            }
        }

        visualisers.add(Center.of(c));

        return visualisers;
    }

}