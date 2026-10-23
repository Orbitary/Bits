/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.world.volume;

import org.joml.Quaternionf;

import xyz.orbitary.bits.mc.world.Vec3;


final class Frame {
    private final Vec3 center;
    // m[world * 3 + local]: world = m * local, so local = transpose(m) * world
    private final double[] m = new double[9];

    Frame(Vec3 center, Quaternionf rotation) {
        this.center = center;

        double rx = rotation.x, ry = rotation.y, rz = rotation.z, rw = rotation.w;
        double length = Math.sqrt(rx * rx + ry * ry + rz * rz + rw * rw);
        double x = rx / length, y = ry / length, z = rz / length, w = rw / length;

        m[0] = 1 - 2 * (y * y + z * z);
        m[1] = 2 * (x * y - z * w);
        m[2] = 2 * (x * z + y * w);
        m[3] = 2 * (x * y + z * w);
        m[4] = 1 - 2 * (x * x + z * z);
        m[5] = 2 * (y * z - x * w);
        m[6] = 2 * (x * z - y * w);
        m[7] = 2 * (y * z + x * w);
        m[8] = 1 - 2 * (x * x + y * y);
    }

    Vec3 center() {
        return center;
    }

    Vec3 toLocal(Vec3 point) {
        double dx = point.x() - center.x(), dy = point.y() - center.y(), dz = point.z() - center.z();
        return new Vec3(
          m[0] * dx + m[3] * dy + m[6] * dz,
          m[1] * dx + m[4] * dy + m[7] * dz,
          m[2] * dx + m[5] * dy + m[8] * dz
        );
    }

    double boxHalfExtent(int axis, double hx, double hy, double hz) {
        return Math.abs(m[axis * 3]) * hx + Math.abs(m[axis * 3 + 1]) * hy + Math.abs(m[axis * 3 + 2]) * hz;
    }

    double ellipsoidHalfExtent(int axis, double rx, double ry, double rz) {
        double a = m[axis * 3] * rx, b = m[axis * 3 + 1] * ry, c = m[axis * 3 + 2] * rz;
        return Math.sqrt(a * a + b * b + c * c);
    }

    double cylinderHalfExtent(int axis, double rx, double rz, double halfHeight) {
        double a = m[axis * 3] * rx, c = m[axis * 3 + 2] * rz;
        return Math.sqrt(a * a + c * c) + Math.abs(m[axis * 3 + 1]) * halfHeight;
    }

}
