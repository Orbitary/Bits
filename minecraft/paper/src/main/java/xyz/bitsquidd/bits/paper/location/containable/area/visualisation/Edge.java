/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.paper.location.containable.area.visualisation;

import org.bukkit.util.Vector;

import xyz.bitsquidd.bits.paper.location.containable.area.visualisation.impl.RegionVisualiser;
import xyz.bitsquidd.bits.paper.location.wrapper.BlockPos;

import java.util.HashSet;
import java.util.Set;

public final class Edge extends RegionVisualiser {
    public static final double STEP = 2.0;
    private final Set<BlockPos> points;

    private Edge(Set<BlockPos> points) {
        this.points = points;
    }


    public static Edge of(Set<BlockPos> points) {
        return new Edge(points);
    }

    public static Edge straight(BlockPos start, BlockPos end) {
        double distance = start.distance(end);
        Set<BlockPos> points = new HashSet<>();
        int steps = (int)(distance / STEP);
        Vector vectorInbetween = end.asVector().subtract(start.asVector()).divide(new Vector(steps, steps, steps));

        for (int i = 0; i <= steps; i++) {
            BlockPos point = start.add(vectorInbetween.clone().multiply(i));
            points.add(point);
        }

        return new Edge(points);
    }

    public static Edge arc(BlockPos center, double radius, double startAngle, double endAngle, double pitch, double yaw) {
        Set<BlockPos> points = new HashSet<>();

        double arcLength = Math.toRadians(endAngle - startAngle) * radius;
        int steps = Math.max(1, (int)(arcLength / STEP));
        double angleStep = (endAngle - startAngle) / steps;

        double pitchRad = Math.toRadians(pitch);
        double yawRad = Math.toRadians(yaw);

        for (int i = 0; i <= steps; i++) {
            double angle = startAngle + angleStep * i;
            double rad = Math.toRadians(angle);

            double localX = radius * Math.cos(rad);
            double localY = 0;
            double localZ = radius * Math.sin(rad);

            double y1 = localY * Math.cos(pitchRad) - localZ * Math.sin(pitchRad);
            double z1 = localY * Math.sin(pitchRad) + localZ * Math.cos(pitchRad);

            double x2 = localX * Math.cos(yawRad) + z1 * Math.sin(yawRad);
            double z2 = -localX * Math.sin(yawRad) + z1 * Math.cos(yawRad);

            points.add(BlockPos.of(
              center.x + x2,
              center.y + y1,
              center.z + z2
            ));
        }

        return new Edge(points);
    }


    @Override
    protected Set<BlockPos> getPositions() {
        return Set.copyOf(points);
    }

    @Override
    protected int getColor() {
        return 0xaaaaaa;
    }

}
