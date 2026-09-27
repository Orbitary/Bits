/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.util.reflection;

import io.github.classgraph.ClassInfo;


/**
 * Controls which classes are included in a scan.
 *
 * <p>Start from {@link ScannerFlags#DEFAULT} and compose with the {@code with*} methods:
 * <pre>{@code
 *   ScannerFlags flags = ScannerFlags.DEFAULT.withAbstract().withDeprecated();
 * }</pre>
 *
 * @since 0.0.13
 */
public record ScannerFlags(
  boolean includeAbstract,
  boolean includeDeprecated,
  boolean includeInnerClasses
) {
    public static final ScannerFlags DEFAULT = new ScannerFlags(false, false, false);

    public boolean isValid(ClassInfo info) {
        if (!includeAbstract && (info.isAbstract() || info.isInterface())) return false;
        if (!includeDeprecated && info.hasAnnotation(Deprecated.class.getName())) return false;
        if (!includeInnerClasses && info.isInnerClass()) return false;
        return true;
    }

    public ScannerFlags withAbstract() {
        return new ScannerFlags(true, includeDeprecated, includeInnerClasses);
    }

    public ScannerFlags withDeprecated() {
        return new ScannerFlags(includeAbstract, true, includeInnerClasses);
    }

    public ScannerFlags withInnerClasses() {
        return new ScannerFlags(includeAbstract, includeDeprecated, true);
    }

}
