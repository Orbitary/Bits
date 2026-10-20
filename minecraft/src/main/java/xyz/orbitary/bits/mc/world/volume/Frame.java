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
    // m[world][local]: world = m * local, so local = transpose(m) * world
    private final double[][] m = new double[3][3];

    Frame(Vec3 center, Quaternionf rotation) {
        this.center = center;

        double rx = rotation.x, ry = rotation.y, rz = rotation.z, rw = rotation.w;
        double length = Math.sqrt(rx * rx + ry * ry + rz * rz + rw * rw);
        double x = rx / length, y = ry / length, z = rz / length, w = rw / length;

        m[0][0] = 1 - 2 * (y * y + z * z);
        m[0][1] = 2 * (x * y - z * w);
        m[0][2] = 2 * (x * z + y * w);
        m[1][0] = 2 * (x * y + z * w);
        m[1][1] = 1 - 2 * (x * x + z * z);
        m[1][2] = 2 * (y * z - x * w);
        m[2][0] = 2 * (x * z - y * w);
        m[2][1] = 2 * (y * z + x * w);
        m[2][2] = 1 - 2 * (x * x + y * y);
    }

    Vec3 center() {
        return center;
    }

    Vec3 toLocal(Vec3 point) {
        double dx = point.x() - center.x(), dy = point.y() - center.y(), dz = point.z() - center.z();
        return new Vec3(
          m[0][0] * dx + m[1][0] * dy + m[2][0] * dz,
          m[0][1] * dx + m[1][1] * dy + m[2][1] * dz,
          m[0][2] * dx + m[1][2] * dy + m[2][2] * dz
        );
    }

    double boxHalfExtent(int axis, double hx, double hy, double hz) {
        return Math.abs(m[axis][0]) * hx + Math.abs(m[axis][1]) * hy + Math.abs(m[axis][2]) * hz;
    }

    double ellipsoidHalfExtent(int axis, double rx, double ry, double rz) {
        double a = m[axis][0] * rx, b = m[axis][1] * ry, c = m[axis][2] * rz;
        return Math.sqrt(a * a + b * b + c * c);
    }

    double cylinderHalfExtent(int axis, double rx, double rz, double halfHeight) {
        double a = m[axis][0] * rx, c = m[axis][2] * rz;
        return Math.sqrt(a * a + c * c) + Math.abs(m[axis][1]) * halfHeight;
    }

}
