/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.mc.module.command;

import java.util.List;
import java.util.function.Function;


/**
 * A Java type built from several Brigadier arguments, such as a location from x, y, z and a world. Each part is
 * read as its own argument named {@code <parameter>_<part>}, and {@link #combiner} builds the value from the parts
 * in declaration order.
 *
 * @param parts    the arguments the value is read from, in order
 * @param combiner builds the value from the parts' values
 * @param <T>      the composite type
 *
 * @since 0.1.6
 */
public record BrigadierComposite<T>(List<Part> parts, Function<Object[], T> combiner) {

    /**
     * One argument of a composite type.
     *
     * @param name the suffix appended to the parameter name
     * @param type the Java type the part is read as, looked up in {@link BrigadierArguments}
     *
     * @since 0.1.6
     */
    public record Part(String name, Class<?> type) {}

}
